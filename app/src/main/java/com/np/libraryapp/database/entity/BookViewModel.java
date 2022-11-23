package com.np.libraryapp.database.entity;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.np.libraryapp.database.repository.BookRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookViewModel extends AndroidViewModel {

    private final BookRepository bookRepository;
    private final LiveData<List<Book>> books;

    public BookViewModel(@NotNull Application application) {
        super(application);
        bookRepository = new BookRepository(application);
        books = bookRepository.findAllBooks();
    }

    public LiveData<List<Book>> findAll() {
        return books;
    }

    public void insert(Book book) {
        bookRepository.insert(book);
    }

    public void update(Book book) {
        bookRepository.update(book);
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }
}
