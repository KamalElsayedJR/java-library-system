package com.flairs.library_api.repository;

import com.flairs.library_api.model.Book;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    private final Map<Long, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Book> findAll() {
        List<Book> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparing(Book::id));
        return all;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsByIsbn(String isbn) {
        return store.values().stream().anyMatch(b -> b.isbn().equals(isbn));
    }

    public Book save(Book book) {
        Book toStore = book.id() == null ? book.withId(nextId.getAndIncrement()) : book;
        store.put(toStore.id(), toStore);
        return toStore;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
