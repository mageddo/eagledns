package se.unlogic.eagledns;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Zone;
import org.xbill.DNS.ZoneTransferException;


public class CachedSecondaryZone extends CachedPrimaryZone {

	private Logger log = Logger.getLogger(this.getClass());
	
	private SecondaryZone secondaryZone;
	private Long lastChecked;
	
	public CachedSecondaryZone(ZoneProvider zoneProvider, SecondaryZone secondaryZone) {

		super(null, zoneProvider);
		this.secondaryZone = secondaryZone;
		this.update();
		
		if(this.zone == null && this.secondaryZone.getZoneBackup() != null){
			
			log.info("Using backup zone data for sedondary zone " + this.secondaryZone.getZoneName());
			
			this.zone = this.secondaryZone.getZoneBackup();
		}
	}

	
	public SecondaryZone getSecondaryZone() {
	
		return secondaryZone;
	}

	
	public void setSecondaryZone(SecondaryZone secondaryZone) {
	
		this.secondaryZone = secondaryZone;
	}
	
	public Long getLastChecked() {
	
		return lastChecked;
	}
	
	public void setLastChecked(Long lastChecked) {
	
		this.lastChecked = lastChecked;
	}


	/**
	 * Updates this secondary zone from the primary zone
	 */
	public void update() {

		
		try {			
			this.zone = new Zone(this.secondaryZone.getZoneName(), DClass.IN, this.secondaryZone.getRemoteServerAddress());
			
			this.secondaryZone.setZoneBackup(zone);
			
			this.zoneProvider.zoneUpdated(this.secondaryZone);
			
			log.info("Zone " + this.secondaryZone.getZoneName() + " successfully transfered from server " + this.secondaryZone.getRemoteServerAddress());
			
		} catch (IOException e) {

			log.warn("Unable to transfer zone " + this.secondaryZone.getZoneName() + " from server " + this.secondaryZone.getRemoteServerAddress() + ", " + e);
			
		} catch (ZoneTransferException e) {

			log.warn("Unable to transfer zone " + this.secondaryZone.getZoneName() + " from server " + this.secondaryZone.getRemoteServerAddress() + ", " + e);
			
		}finally{
			
			this.lastChecked = System.currentTimeMillis();
		}
	}
}