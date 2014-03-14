package database;

import android.provider.BaseColumns;

public class ActionContract {

	// DB specific constants
	public static final String DB_NAME = "timeline.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE = "connections";

	public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC";

	//
	public class Column {
		public static final String ID = BaseColumns._ID;
		public static final String MAC = "user";
		public static final String NAME = "name";
		public static final String ACTION = "action";
		public static final String CREATED_AT = "created_at";
	}
}
