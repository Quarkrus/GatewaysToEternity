package shadows.gateways.gate;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import shadows.placebo.json.ItemAdapter;
import shadows.placebo.json.JsonUtil;
import shadows.placebo.json.PSerializer;
import shadows.placebo.json.PlaceboJsonReloadListener;

public interface WaveEntity {

	public static BiMap<ResourceLocation, PSerializer<WaveEntity>> SERIALIZERS = HashBiMap.create();

	/**
	 * Creates the entity to be spawned in the current wave.
	 * @param level
	 * @return The entity, or null if an error occured.  Null will end the gate.
	 */
	public LivingEntity createEntity(Level level);

	public Component getDescription();

	public AABB getAABB(double x, double y, double z);

	public boolean shouldFinalizeSpawn();

	public PSerializer<WaveEntity> getSerializer();

	public static class StandardWaveEntity implements WaveEntity {

		static final PSerializer<WaveEntity> SERIALIZER = PSerializer.<WaveEntity>autoRegister("Std Wave Entity", StandardWaveEntity.class).build(true);

		protected final EntityType<?> type;
		protected final CompoundTag tag;

		public StandardWaveEntity(EntityType<?> type, @Nullable CompoundTag tag) {
			this.type = type;
			this.tag = tag == null ? new CompoundTag() : tag;
			this.tag.putString("id", EntityType.getKey(type).toString());
		}

		@Override
		public LivingEntity createEntity(Level level) {
			Entity ent = EntityType.loadEntityRecursive(this.tag, level, Function.identity());
			return ent instanceof LivingEntity l ? l : null;
		}

		@Override
		public Component getDescription() {
			return Component.translatable(type.getDescriptionId());
		}

		@Override
		public AABB getAABB(double x, double y, double z) {
			return this.type.getAABB(x, y, z);
		}

		@Override
		public boolean shouldFinalizeSpawn() {
			return this.tag.size() == 1 || this.tag.getBoolean("ForceFinalizeSpawn");
		}

		@Override
		public PSerializer<WaveEntity> getSerializer() {
			return SERIALIZER;
		}

		public JsonObject write() {
			JsonObject entityData = new JsonObject();
			entityData.addProperty("entity", EntityType.getKey(type).toString());
			if (tag != null) entityData.add("nbt", ItemAdapter.ITEM_READER.toJsonTree(tag));
			return entityData;
		}

		public static WaveEntity read(JsonObject obj) {
			EntityType<?> type = JsonUtil.getRegistryObject(obj, "entity", ForgeRegistries.ENTITY_TYPES);
			CompoundTag nbt = obj.has("nbt") ? ItemAdapter.ITEM_READER.fromJson(obj.get("nbt"), CompoundTag.class) : null;
			return new StandardWaveEntity(type, nbt);
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeRegistryId(ForgeRegistries.ENTITY_TYPES, type);
		}

		public static WaveEntity read(FriendlyByteBuf buf) {
			return new StandardWaveEntity(buf.readRegistryIdSafe(EntityType.class), null);
		}

	}

	public static void initSerializers() {
		SERIALIZERS.put(PlaceboJsonReloadListener.DEFAULT, StandardWaveEntity.SERIALIZER);
	}

}