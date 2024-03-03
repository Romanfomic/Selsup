import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {

    private final int requestLimit;
    private final long timeIntervalMillis;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final Object lock = new Object();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimit = requestLimit;
        this.timeIntervalMillis = timeUnit.toMillis(1);
    }

    public void createDocument(Document document, String signature) {
        synchronized (lock) {
            while (requestCount.get() >= requestLimit) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Simulating API call
            System.out.println("Creating document: " + document.toString() + " with signature: " + signature);
            requestCount.incrementAndGet();
            lock.notifyAll();
        }
    }

    // Inner class representing the document
    static class Document {
        private String participantInn;
        // Other fields omitted for brevity

        @Override
        public String toString() {
            return "Document{" +
                    "participantInn='" + participantInn + '\'' +
                    // Other fields omitted for brevity
                    '}';
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 5); // Example: 5 requests per second

        // Simulating multiple threads making API calls
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    crptApi.createDocument(new Document(), "signature");
                    try {
                        Thread.sleep(1000); // Simulate some processing time
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
