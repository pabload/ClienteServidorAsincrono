
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;


public class PrincipalServidor  {
    private static Map<String, PrintWriter> usuarios = new HashMap<>();
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
            in= new Scanner(socket.getInputStream());
            out= new PrintWriter(socket.getOutputStream(),true);
             while (true) {                    
                    out.println("SUBMITNAME");
                    name= in.nextLine();
                    if(name==null || name.equalsIgnoreCase("quit") || name.isEmpty()){
                        continue;
                    }
                    synchronized(usuarios){
                        if(!usuarios.containsValue(name)){
                            usuarios.put(name, out);
                            out.println("NAMEACCEPTED " + name);
                
                            for(PrintWriter writer : usuarios.values()){
                                writer.println("MESSAGE " + name + " joined");
                            }
                           //writers.add(out);
                            break;
                        }
                    }
                }
            while (true) {                
               String input = in.nextLine();
               int inp = input.indexOf(' ');
               if(input.startsWith("/") && !input.startsWith("/quit")){
                   String nombre = input.substring(1, inp);
                   String mensaje = input.substring(inp, input.length());
                   if (usuarios.containsKey(nombre)) {
                       usuarios.get(nombre).println("MESSAGE (PM) " + mensaje);
                       usuarios.get(name).println("MESSAGE (PM-" + nombre + ") " + mensaje);
                   }
               }
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
                }
            
           
             /*for (PrintWriter writer : writers) {
               writer.println("MESSAGE "+name+": "+input);
             }    */            
            }
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            if(out != null || name != null){
                System.out.println(name+" is leaving");
                usuarios.remove(name);
                for (PrintWriter writer : usuarios.values()) {
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
