package com.np.libraryapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.np.libraryapp.database.entity.Book;
import com.np.libraryapp.database.entity.BookViewModel;
import com.np.libraryapp.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "MainActivity";

    private final int NEW_BOOK_ACTIVITY_REQUEST_CODE = 1;
    private final int EDIT_BOOK_ACTIVITY_REQUEST_CODE = 2;

    private BookViewModel bookViewModel;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter bookAdapter = new BookAdapter();
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAll().observe(this, bookAdapter::setBooks);

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this, EditBookActivity.class);
            addBookIntent.launch(intent);
            //startActivityForResult(intent, NEW_BOOK_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private ActivityResultLauncher<Intent> editBookIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() != RESULT_OK) {
                    Snackbar.make(
                                    findViewById(R.id.coordinator_layout),
                                    getString(R.string.empty_not_saved),
                                    Snackbar.LENGTH_LONG
                            )
                            .show();
                    return;
                }

                Intent data = result.getData();
                Book book = new Book(
                        data.getStringExtra(EditBookActivity.EXTRA_BOOK_TITLE),
                        data.getStringExtra(EditBookActivity.EXTRA_BOOK_AUTHOR)
                );
                book.setId(data.getIntExtra(EditBookActivity.EXTRA_BOOK_ID, -1));
                bookViewModel.update(book);
                Snackbar.make(
                                findViewById(R.id.coordinator_layout),
                                getString(R.string.book_edited),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            });

    private ActivityResultLauncher<Intent> addBookIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    Snackbar.make(
                                    findViewById(R.id.coordinator_layout),
                                    getString(R.string.empty_not_saved),
                                    Snackbar.LENGTH_LONG
                            )
                            .show();
                    return;
                }

                Intent data = result.getData();
                Book book = new Book(
                        data.getStringExtra(EditBookActivity.EXTRA_BOOK_TITLE),
                        data.getStringExtra(EditBookActivity.EXTRA_BOOK_AUTHOR)
                );
                bookViewModel.insert(book);
                Snackbar.make(
                                findViewById(R.id.coordinator_layout),
                                getString(R.string.book_added),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            });

    private class BookHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private ImageView editIcon;
        private ImageView deleteIcon;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            bookTitleTextView = itemView.findViewById(R.id.book_title_label);
            bookAuthorTextView = itemView.findViewById(R.id.book_author_label);
            editIcon = itemView.findViewById(R.id.edit);
            deleteIcon = itemView.findViewById(R.id.delete);
        }

        public void bind(Book book) {
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());

            editIcon.setOnClickListener((view) -> {
                Intent intent = new Intent(getApplicationContext(), EditBookActivity.class);
                intent.putExtra(EditBookActivity.EXTRA_BOOK_ID, book.getId());
                intent.putExtra(EditBookActivity.EXTRA_BOOK_TITLE, book.getTitle());
                intent.putExtra(EditBookActivity.EXTRA_BOOK_AUTHOR, book.getAuthor());
                editBookIntent.launch(intent);
                //startActivityForResult(intent, EDIT_BOOK_ACTIVITY_REQUEST_CODE);
            });

            deleteIcon.setOnClickListener((view) -> {
                bookViewModel.delete(book);
                Snackbar.make(
                                findViewById(R.id.coordinator_layout),
                                getString(R.string.deleted),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            });
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                Book book = books.get(position);
                holder.bind(book);
            } else {
                Log.d(LOG_TAG, "No books");
            }
        }

        @Override
        public int getItemCount() {
            return books == null ?
                    0 : books.size();
        }

        public void setBooks(List<Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}