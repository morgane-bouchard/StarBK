package fr.istic.mob.starbk.DataBaseManager;

import android.provider.BaseColumns;

import fr.istic.mob.starbk.StarContract;

public interface DataBase extends StarContract {
    int DbVersion = 1;
    String DbName = "starBK.db";

    interface DataVersions {
        String content_path = "versions";

        interface DataVersionColumns extends BaseColumns {
            String filename = "filename";
            String fileVersion = "file_version";
            String createdAt = "created_at";
        }
    }
}
