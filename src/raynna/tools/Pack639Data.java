package raynna.tools;

import com.alex.store.Index;
import com.alex.store.Store;
import com.alex.utils.Constants;
import raynna.app.Settings;
import raynna.core.cache.Cache;

import java.io.IOException;

/**
 * Packs data from a revision 639 cache into the server cache using the 639 offsets,
 * so 639 data does not collide with existing server data.
 *
 * Models:  packed at (rawId + _639_MODEL_OFFSET)   → ObjectDefinitions.readValues639() adds this offset when reading
 * Objects: packed at (rawId + _639_OBJECTS_OFFSET) → getObjectDefinitions() uses id >>> 8 / id & 0xff to locate them,
 *          and readValueLoop routes to readValues639() when id >= _639_OBJECTS_OFFSET
 */
public class Pack639Data {

	private static final String CACHE_639_PATH = "C:/Users/andre/Desktop/727-source/data/voidcache/";
	private static final int MAP_INDEX = 5;

	// Both the 639 void cache and server store object definitions in index 16
	// (archive = id >>> 8, file = id & 0xff)
	private static final int OBJECT_DEFINITIONS_INDEX = 16;

	public static void main(String[] args) throws IOException {
		Cache.init();

		Store cache639 = new Store(CACHE_639_PATH);

		packModels(cache639);
		packObjects(cache639);
		packMapFiles(cache639);
	}

	private static void packModels(Store cache639) {
		System.out.println("Packing 639 models at offset " + Settings._639_MODEL_OFFSET + "...");
		Index models = Cache.STORE.getIndexes()[Constants.MODELS_INDEX];
		Index models639 = cache639.getIndexes()[Constants.MODELS_INDEX];

		int packed = 0;
		for (int i = 0; i <= models639.getLastArchiveId(); i++) {
			byte[] data = models639.getFile(i);
			if (data == null)
				continue;
			int targetId = Settings._639_MODEL_OFFSET + i;
			models.putFileNoRewriteTable(targetId, 0, data);
			packed++;
		}

		models.rewriteTable();
		System.out.println("Models done. Packed " + packed + " (slots " + Settings._639_MODEL_OFFSET + "-" + (Settings._639_MODEL_OFFSET + models639.getLastArchiveId()) + ").");
	}

	private static void packObjects(Store cache639) {
		System.out.println("Packing 639 object definitions at offset " + Settings._639_OBJECTS_OFFSET + "...");
		// 639 void cache uses index 16: archive = id >>> 8, file = id & 0xff
		Index objects = Cache.STORE.getIndexes()[OBJECT_DEFINITIONS_INDEX];
		Index objects639 = cache639.getIndexes()[OBJECT_DEFINITIONS_INDEX];

		int packed = 0;
		int lastArchive = objects639.getLastArchiveId();
		for (int archive = 0; archive <= lastArchive; archive++) {
			int lastFile = objects639.getLastFileId(archive);
			for (int file = 0; file <= lastFile; file++) {
				byte[] data = objects639.getFile(archive, file);
				if (data == null)
					continue;
				int rawId = (archive << 8) | file;
				int targetId = Settings._639_OBJECTS_OFFSET + rawId;
				objects.putFileNoRewriteTable(targetId >>> 8, targetId & 0xff, data);
				packed++;
			}
		}

		objects.rewriteTable();
		System.out.println("Objects done. Packed " + packed + " object definitions.");
	}

	/**
	 * Packs the land (l) files for each 639 region from the 639 cache
	 * into the server cache index 5 as unencrypted data.
	 * The server's map archive keys for each region must be {0,0,0,0}
	 * in data/map/archiveKeys/unpacked/<regionId>.txt.
	 *
	 * After running this, the server no longer needs the 639 cache at runtime —
	 * it reads the land file directly from its own cache like any other region.
	 */
	private static void packMapFiles(Store cache639) {
		System.out.println("Packing 639 land files...");
		Index mapIndex    = Cache.STORE.getIndexes()[MAP_INDEX];
		Index mapIndex639 = cache639.getIndexes()[MAP_INDEX];

		for (int regionId : Settings._639_MAP_IDS) {
			int regionX = (regionId >> 8) * 64;
			int regionY = (regionId & 0xff) * 64;
			int mx = (regionX >> 3) / 8;
			int my = (regionY >> 3) / 8;
			String landName = "l" + mx + "_" + my;

			int landId639    = mapIndex639.getArchiveId(landName);
			int landIdServer = mapIndex.getArchiveId(landName);

			if (landId639 == -1) {
				System.out.println("  Region " + regionId + ": '" + landName + "' not found in 639 cache — skipped.");
				continue;
			}

			// Read the decrypted land bytes from the 639 cache (GE is unencrypted)
			byte[] landData = mapIndex639.getFile(landId639, 0);
			if (landData == null) {
				System.out.println("  Region " + regionId + ": '" + landName + "' data is null in 639 cache (encrypted?) — skipped.");
				continue;
			}

			if (landIdServer == -1) {
				System.out.println("  Region " + regionId + ": '" + landName + "' not in server cache — skipped (run packMapFiles after server has the archive).");
				continue;
			}

			// Write the decrypted data back to the server cache at the existing archive slot.
			// XTEA keys for this region must be {0,0,0,0} so the server reads it unencrypted.
			mapIndex.putFile(landIdServer, 0, landData);
			System.out.println("  Region " + regionId + ": packed '" + landName + "' (" + landData.length + " bytes, server archiveId=" + landIdServer + ").");
		}

		mapIndex.rewriteTable();
		System.out.println("Land files done.");
	}
}