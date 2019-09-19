
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;


public class PrincipalServidor  {
    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    
    public static void main(String[] args) throws IOException {
        System.out.println("the chat server is running");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try(ServerSocket listener = new ServerSocket(59001)){
            while (true) {                
                pool.execute(new Handler(listener.accept()));
            }
 
        }
        
    }
    private static class Handler implements Runnable{
        private String name;
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
    
    
    public  Handler(Socket socket){
       this.socket=socket;
    }
    
    public void run (){
        try {
            in= new Scanner(socket.getInputStream());
            out= new PrintWriter(socket.getOutputStream(),true);
            
            while (true) {                
              out.println("SUBMITNAME");
              name= in.nextLine();
               if (name==null) {
                    if (!name.contains(name)) {
                       names.add(name);
                       break;
                   }
                }
            }
            out.println("NAMEACCEPTED"+name);
            for (PrintWriter writer : writers) {
                writers.add(out);
            }
            while (true) {                
               String input = in.nextLine();
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
            }
             for (PrintWriter writer : writers) {
               writer.println("MESSAGE"+name+": "+input);
             }                
            }
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            if (out != null) {
               writers.remove(out);
            }
            if (name != null) {
                System.out.println(name+"is leaving");
                names.remove(name);
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE"+names+" has left");
                }
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
        
    }
    }
    
}
