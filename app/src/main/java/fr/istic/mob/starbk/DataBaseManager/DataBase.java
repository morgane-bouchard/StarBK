package fr.istic.mob.starbk.DataBaseManager;

import android.provider.BaseColumns;

import fr.istic.mob.starbk.StarContract;

public interface DataBase extends StarContract {
    int DB_VERSION = 1;
    String DB_NAME = "starBK.db";

    interface DataVersions {
        String CONTENT_PATH = "versions";

        interface DataVersionColumns extends BaseColumns {
            String FILENAME = "filename";
            String FILE_VERSION = "file_version";
            String CREATED_AT = "created_at";
        }
    }
}
