package de.gdiservice.cmdserver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Tokenizer {
    

    

    
    
    static String[] getTokens(String s) {
        
        System.out.println(s);
        List<String> tokens = new ArrayList<>();
        
          
        Character esChar = null;
        
        int tokenStart = 0;
        int idx = 0;
        
        for (int i=0, count=s.length(); i<count; i++) {

            char ch = s.charAt(i);            
            if (ch=='"' || ch=='\'') {
                if (esChar ==null) {
                    esChar = ch;
                    idx++;
                } else {
                    if (esChar.equals(ch)) {
                        esChar=null;
                    }
                    idx++;
                }
            } else if (ch == ' ') {
                if (esChar == null) {
                    String token = s.substring(tokenStart, idx);
                    // System.out.println(token);
                    final String t = token.replace("\"", "");
                    tokens.add(t);
                    tokenStart = idx+1;
                    idx++;
                }
                else {                    
                    idx++;
                }
            } else {
                idx++;
            }
        }
        
        tokens.add(s.substring(tokenStart, idx));
        return tokens.toArray(new String[tokens.size()]);
        
    }
    
    
    public static void main(String[] args) {
        String s = "-f \"PostgreSQL\" PG:\"host='pgsql' port='5432' dbname='kvwmapsp' user='kvwmap' ACTIVE_SCHEMA=testschema_ralf \" GMLAS:/var/www/tmp/temp.gml_2.gml -oo REMOVE_UNUSED_LAYERS=YES -oo XSD=/var/www/html/modell/xsd/5.1/XPlanung-Operationen.xsd -lco SCHEMA=testschema_ralf";
        // String s = "-f \"PostgreSQL\" PG:\"host='pgsql'";
        String[] tokens = getTokens(s);
        for (int i=0; i<tokens.length; i++) {
            System.out.println(tokens[i]);
        }
    }
    

}
