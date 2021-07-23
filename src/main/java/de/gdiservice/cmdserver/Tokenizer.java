package de.gdiservice.cmdserver;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    
    static String[] getTokens(String s) {
        
        System.out.println(s);
        List<String> tokens = new ArrayList<>();
        
        Character esChar = null;
        
        StringBuilder sb = new StringBuilder();
        
        for (int i=0, count=s.length(); i<count; i++) {            
            char ch = s.charAt(i);
            if (ch == '\\' && i<count-1 && (s.charAt(i+1)=='"' || s.charAt(i+1)=='\\')) {
                sb.append(s.charAt(i+1));
                i++;
            } else {
                if (ch=='"' || ch=='\'') {
                    if (esChar == null) {
                        esChar = ch;
                    } else {
                        if (esChar.equals(ch)) {
                            esChar=null;
                        } else {
                          sb.append(ch);                        
                        }
                    }
                } else if (ch == ' ') {
                    if (esChar == null) {
                        if (sb.length()>0) {
                            tokens.add(sb.toString());
                            sb.setLength(0);
                        }
                    }
                    else {                    
                        sb.append(ch);
                    }
                } else {
                    sb.append(ch);
                }
            }
        }
        if (sb.length()>0) {
            tokens.add(sb.toString());
        }
        return tokens.toArray(new String[tokens.size()]);
        
    }
    
    
    public static void main(String[] args) {
        // String s = "-f \"PostgreSQL\" PG:\"host='pgsql' port='5432' dbname='kvwmapsp' user='kvwmap' ACTIVE_SCHEMA=testschema_ralf \" GMLAS:/var/www/tmp/temp.gml_2.gml -oo REMOVE_UNUSED_LAYERS=YES -oo XSD=/var/www/html/modell/xsd/5.1/XPlanung-Operationen.xsd -lco SCHEMA=testschema_ralf";
        String s = "-lco FID=gid -f PostgreSQL -lco GEOMETRY_NAME=the_geom -lco precision=NO    -nlt PROMOTE_TO_MULTI -nln afrei_1polygon908580 -a_srs EPSG:2398 -append PG:\"host='pgsql' port='5432' dbname='kvwmapsp' user='k\\\\\\\"vwmap' password='-1$ß~bN\\\"6&ZWPe}B_YpA+l' active_schema=custom_shapes\" \"/var/www/data/upload/11/Frei 1Polygon.dbf\"   ";
        // String s = "-f \"PostgreSQL\" PG:\"host='pgsql'";
        String[] tokens = getTokens(s);
        for (int i=0; i<tokens.length; i++) {
            System.out.println("T=\t"+tokens[i]);
        }
        System.out.println("End");
    }   

}
