import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UITest {



    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }


    void testPrintLoadingBar() {
        // Call the printLoadingBar method with 50% completion
        UserInterface.printLoadingBar(50, 100, 10);

        // Build the expected output
        String expectedOutput = "\r[=====     ] 50% complete";
        
        // Assert that the output contains the expected progress bar
        //assertTrue(outContent.toString().contains(expectedOutput));

        // Optionally, test the output at 100% completion
        outContent.reset(); // Clear the output stream before the next test
        UserInterface.printLoadingBar(100, 100, 10);
        expectedOutput = "\r[==========] 100% complete\n"; // Expect a new line at the end
        //assertEquals(expectedOutput, outContent.toString());
    }
}
