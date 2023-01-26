package shadows.gateways.event;

import net.minecraftforge.event.entity.EntityEvent;
import shadows.gateways.entity.GatewayEntity;

public abstract class GateEvent extends EntityEvent {

	public GateEvent(GatewayEntity entity) {
		super(entity);
	}

	@Override
	public GatewayEntity getEntity() {
		return (GatewayEntity) super.getEntity();
	}

	/**
	 * GateEvent$Opened is fired when a Gateway is opened.
	 */
	public static class Opened extends GateEvent {

		public Opened(GatewayEntity entity) {
			super(entity);
		}

	}

	/**
	 * GateEvent$Completed is fired when a Gateway is successfully completed.
	 */
	public static class Completed extends GateEvent {

		public Completed(GatewayEntity entity) {
			super(entity);
		}

	}

	/**
	 * GateEvent$WaveEnd is fired when a wave is completed, but before the current wave counter is incremented.
	 */
	public static class WaveEnd extends GateEvent {

		public WaveEnd(GatewayEntity entity) {
			super(entity);
		}

	}

	/**
	 * GateEvent$Failed is fired when a gateway is failed for any reason.
	 */
	public static class Failed extends GateEvent {

		public Failed(GatewayEntity entity) {
			super(entity);
		}

	}

}