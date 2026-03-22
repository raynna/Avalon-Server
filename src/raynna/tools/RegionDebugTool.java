package raynna.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;

import java.util.Arrays;

public class RegionDebugTool {

    private static final int MAPS_INDEX = 5;

    private static final String CACHE_PATH = "data/cache/"; // change to compare

    private static final int REGION_ID = 12598;

    private static final int[] KEYS = {504856351,
            446930567,
            -1265381907,
            -565095951};

    public static void main(String[] args) throws Exception {

        int regionX = REGION_ID >> 8;
        int regionY = REGION_ID & 0xFF;

        String mapName  = "m" + regionX + "_" + regionY;
        String landName = "l" + regionX + "_" + regionY;

        System.out.println("===== REGION DEBUG =====");
        System.out.println("Region: " + REGION_ID + " (" + regionX + "," + regionY + ")");
        System.out.println("Cache: " + CACHE_PATH);
        System.out.println();
        CacheLibrary cache = new CacheLibrary(CACHE_PATH, false, null);

        try {

            var index = cache.index(MAPS_INDEX);

            int mapId  = index.archiveId(mapName);
            int landId = index.archiveId(landName);

            System.out.println("Map archive ID: " + mapId);
            System.out.println("Land archive ID: " + landId);
            System.out.println();

            if (mapId == -1 || landId == -1) {
                System.out.println("Archive missing.");
                return;
            }

            var mapSector  = cache.index(MAPS_INDEX).readArchiveSector(mapId);
            var landSector = cache.index(MAPS_INDEX).readArchiveSector(landId);

            System.out.println("Map sector size:  " + mapSector.getData().length);
            System.out.println("Land sector size: " + landSector.getData().length);
            System.out.println();

            Archive mapArchive  = index.archive(mapName);
            Archive landArchive = index.archive(landName, KEYS);

            System.out.println("---- MAP ARCHIVE ----");
            printArchiveInfo(mapArchive);

            System.out.println();
            System.out.println("---- LAND ARCHIVE ----");
            printArchiveInfo(landArchive);

        } finally {
            cache.close();
        }
    }

    private static void printArchiveInfo(Archive archive) {
        if (archive == null) {
            System.out.println("Archive is NULL");
            return;
        }

        System.out.println("ID: " + archive.getId());
        System.out.println("Revision: " + archive.getRevision());
        System.out.println("Compression: " + archive.getCompressionType());
        System.out.println("Contains Data: " + archive.containsData());
        System.out.println("File Count: " + archive.files().length);
        System.out.println("XTEA: " + (archive.getXtea() == null ? "None" : Arrays.toString(archive.getXtea())));
    }
}