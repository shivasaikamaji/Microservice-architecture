
package library_management.service;

import java.util.Collection;

import library_management.model.Book;
import library_management.model.Member;
import library_management.repository.LibraryRepository;

public class LibraryService {

    private final LibraryRepository repository;

    public LibraryService(LibraryRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    public void addBook(Book book) {
        repository.addBook(book);
    }

    public Book findBook(String id) {
        return repository.findBookById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found: " + id));
    }

    public void updateBook(String id, String title, String author) {
        Book book = findBook(id);
        book.setTitle(title);
        book.setAuthor(author);
    }

    public void deleteBook(String id) {
        if (!repository.deleteBook(id)) {
            throw new IllegalStateException(
                    "Book not found or currently borrowed: " + id);
        }
    }

    public Collection<Book> getAllBooks() {
        return repository.getAllBooks();
    }

    public void registerMember(Member member) {
        repository.addMember(member);
    }

    public Member findMember(String id) {
        return repository.findMemberById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Member not found: " + id));
    }

    public void updateMember(String id, String name) {
        Member member = findMember(id);
        member.setName(name);
    }

    public void deleteMember(String id) {
        Member member = findMember(id);

        if (!member.isActive()) {
            throw new IllegalStateException("Member is already inactive");
        }

        member.setActive(false);
    }

    public Collection<Member> getAllMembers() {
        return repository.getAllMembers();
    }
}
