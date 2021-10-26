package pl.inder00.opensource.sectors.basic.manager;

import org.bukkit.Location;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.protocol.ISectorServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SectorManager {

    /**
     * Current sector where plugin is runs
     */
    private static UUID currentSectorUniqueId;
    private static ISector currentSector;
    private static ISectorServer currentSectorEndpoint;

    /**
     * List of all available sectors
     */
    private static ConcurrentHashMap<UUID, ISector> listOfSectors = new ConcurrentHashMap<UUID, ISector>();

    /**
     * List of all sectors
     * @return List of sectors
     */
    public static List<ISector> getSectorsList(){
        return new ArrayList<>(listOfSectors.values());
    }

    /**
     * Gets sector implementation by their location
     * @param location
     * @return ISector / null
     */
    public static ISector getSectorAtLocation( Location location ){
        return listOfSectors.values().stream().filter(sector -> sector.isInLocation(location)).findFirst().orElse(null);
    }

    /**
     * Get sector by the unique id
     * @param uniqueId UUIDv3
     * @return ISector / null
     */
    public static ISector getSectorByUniqueId( UUID uniqueId ){
        return listOfSectors.get( uniqueId );
    }

    /**
     * Count of sectors
     * @return int
     */
    public static int getSectorsCount(){
        return listOfSectors.size();
    }

    /**
     * Gets current sector may return null
     * @return ISocket
     */
    public static ISector getCurrentSector() {
        return currentSector;
    }

    /**
     * Sector endpoint using to handle players data packets between servers
     * @return ISectorServer
     */
    public static ISectorServer getCurrentSectorEndpoint() {
        return currentSectorEndpoint;
    }

    /**
     * Clear list of sectors
     */
    public static void clearSectors(){
        listOfSectors.clear();
    }

    /**
     * Adds sector to list of sectors
     * @param sector Implementation of sector
     */
    public static void addSectorToList( ISector sector ){

        // update current sector if necessary
        if(currentSectorUniqueId.equals(sector.getUniqueId())) currentSector = sector;

        // add sector to map
        listOfSectors.put( sector.getUniqueId(), sector );

    }

    /**
     * Set current sector unique id for identification
     * @param uuid UUIDv3 from sector name
     */
    public static void setCurrentSectorUniqueId( UUID uuid ){
        currentSectorUniqueId = uuid;
    }

    /**
     * Current sector unique id
     * @return UUIDv3 / null
     */
    public static UUID getCurrentSectorUniqueId(){
        return currentSectorUniqueId;
    }

    /**
     * Set current sector endpoint implementation
     * @param endpoint A reference of sector server
     */
    public static void setCurrentSectorEndpoint( ISectorServer endpoint ){
        currentSectorEndpoint = endpoint;
    }
}
