
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
            System.out.println(listener.getInetAddress());
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
            List<String> listanombres= new ArrayList<>(names);
            List<PrintWriter> list = new ArrayList<>(writers);
            in= new Scanner(socket.getInputStream());
            out= new PrintWriter(socket.getOutputStream(),true);
             while (true) {                    
                    out.println("SUBMITNAME");
                    name= in.nextLine();
                    if(name==null || name.contains("quit")){
                        return;
                    }
                    synchronized(name){
                        if(!names.contains(names)){
                            names.add(name);
                            
                            out.println("NAMEACCEPTED " + name);
                
                            for(PrintWriter writer : writers){
                                writer.println("MESSAGE " + name + " joined");
                            }
                           writers.add(out);
                            break;
                        }
                    }
                }
            while (true) {                
               String input = in.nextLine();
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
            }
            
              for (String n : listanombres) {
                   if (input.startsWith("/"+n)) {
                       System.out.println("llego aki");
                   int index=listanombres.indexOf(n);
                   System.out.println(index+"aaaaaaaaaaa");
                   System.out.println(list.size());
                   list.get(index).println("MESSAGE "+name+": "+input);
                   break;
                 
                  }
                   
             }
           
             /*for (PrintWriter writer : writers) {
               writer.println("MESSAGE "+name+": "+input);
             }    */            
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
                    writer.println("MESSAGE "+name+" has left");
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
