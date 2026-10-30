
package library_management.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import library_management.model.Book;
import library_management.model.Member;

/**
 * Stores and retrieves library books and members.
 */
public class LibraryRepository {

    private final Map<String, Book> books = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();

    /**
     * Adds a book to the library.
     *
     * @param book book to add
     */
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (books.containsKey(book.getId())) {
            throw new IllegalArgumentException("Book ID already exists");
        }
        books.put(book.getId(), book);
    }

    public Optional<Book> findBookById(String id) {
        return Optional.ofNullable(books.get(id));
    }

    public boolean deleteBook(String id) {
        Book book = books.get(id);

        if (book == null || !book.isAvailable()) {
            return false;
        }

        books.remove(id);
        return true;
    }

    public Collection<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    /**
     * Registers a library member.
     *
     * @param member member to register
     */
    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (members.containsKey(member.getId())) {
            throw new IllegalArgumentException("Member ID already exists");
        }
        members.put(member.getId(), member);
    }

    public Optional<Member> findMemberById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    public boolean deleteMember(String id) {
        Member member = members.get(id);

        if (member == null) {
            return false;
        }

        members.remove(id);
        return true;
    }

    public Collection<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }
}
