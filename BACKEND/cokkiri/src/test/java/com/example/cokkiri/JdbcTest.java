package com.example.cokkiri;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcTest {

    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConnection(){
        try(Connection con = DriverManager.getConnection(

                "jdbc:mysql://localhost:3306/cokkiri","root","Qwer12345678!")){

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}