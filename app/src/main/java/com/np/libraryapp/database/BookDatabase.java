package com.np.libraryapp.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.np.libraryapp.database.dao.BookDao;
import com.np.libraryapp.database.entity.Book;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {

    private static BookDatabase databaseInstance;
    public static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract BookDao bookDao();

    public static BookDatabase getDatabase(final Context context) {
        if(databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    BookDatabase.class,
                    "book_database"
            )
            .addCallback(roomDatabaseCallback)
            .build();
        }
        return databaseInstance;
    }

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                BookDao dao = databaseInstance.bookDao();
                Book[] books = new Book[] {
                    new Book("Clean code", "Robert C. Martin"),
                    new Book("Krzyżacy", "Henryk Sienkiewicz"),
                    new Book("Wiedźmin", "Andrzej Sapkowski"),
                    new Book("W pustyni i w puszczy", "Henryk Sienkiewicz")
                };

                for (Book book:
                     books) {
                    dao.insert(book);
                }
            });
        }
    };
}
