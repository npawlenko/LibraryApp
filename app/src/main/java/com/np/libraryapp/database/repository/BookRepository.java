package com.np.libraryapp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.np.libraryapp.database.BookDatabase;
import com.np.libraryapp.database.dao.BookDao;
import com.np.libraryapp.database.entity.Book;

import java.util.List;

public class BookRepository {
    private final BookDao bookDao;
    private final LiveData<List<Book>> books;

    public BookRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabase(application);
        bookDao = database.bookDao();
        books = bookDao.findAll();
    }

    public LiveData<List<Book>> findAllBooks() {
        return books;
    }

    public void insert(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDao.insert(book));
    }

    public void update(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDao.update(book));
    }

    public void delete(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDao.delete(book));
    }
}
