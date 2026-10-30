
package library_management.app;

import java.time.LocalDate;

import library_management.model.Book;
import library_management.model.Member;
import library_management.repository.LibraryRepository;
import library_management.service.LibraryService;
import library_management.service.IssueService;

public class LibraryManagementSystem {

    public static void main(String[] args) {

        LibraryRepository repository = new LibraryRepository();
        LibraryService libraryService = new LibraryService(repository);
        IssueService issueService = new IssueService(repository);

        Book book = new Book("B101", "Java Basics", "James");
        Member member = new Member("M101", "Ravi");

        libraryService.addBook(book);
        libraryService.registerMember(member);

        System.out.println("=== Before Issue ===");
        System.out.println("Book available: " + book.isAvailable());

        issueService.issueBook(
                "I101",
                "B101",
                "M101",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 7)
        );

        System.out.println("\n=== After Issue ===");
        System.out.println("Book available: " + book.isAvailable());
        System.out.println("Fine: Rs. "
                + issueService.calculateFine("I101", 5.0));

        issueService.returnBook("I101", LocalDate.of(2026, 10, 10));

        System.out.println("\n=== After Return ===");
        System.out.println("Book available: " + book.isAvailable());
        System.out.println("Fine: Rs. "
                + issueService.calculateFine("I101", 5.0));
    }
}
