package ru.alishev.springcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.repositories.BooksRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage,Boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        } else {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        }
    }

    public List<Book> findAll(Boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(Sort.by("year"));
        } else {
            return booksRepository.findAll();
        }

    }

    public Book findOne(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }
    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {
        return booksRepository.findById(id).map(Book::getOwner).orElse(null);

    }
    @Transactional
    public void assign(int id, Person selectedPerson) {
        Book assignedBook = booksRepository.findById(id).orElse(null);
        if (assignedBook != null) {
            assignedBook.setOwner(selectedPerson);
            assignedBook.setTakenAt(LocalDate.now());
            booksRepository.save(assignedBook);
        }
    }
    @Transactional
    public void release(int id) {
        Book releasedBook = booksRepository.findById(id).orElse(null);
        if (releasedBook != null) {
            releasedBook.setOwner(null);
            releasedBook.setTakenAt(null);
            booksRepository.save(releasedBook);
        }
    }

    @Transactional
    public List<Book> searchBooks(String query) {
        return booksRepository.findByTitleStartingWith(query);
    }

}
