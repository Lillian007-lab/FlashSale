package com.example.flashsale.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Title: UserUtil
 *
 */
public class UserUtil {

    public static String randomStr (int len){
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++){
            int index = (int)(alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static void createUser(int count) throws Exception {

        //Generate User
        List<FlashSaleUser> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            FlashSaleUser user = new FlashSaleUser();
            user.setId(1300000000L + i);
            user.setUsername("user" + i);

            String randomSalt = randomStr(6);
            user.setSalt(randomSalt);

            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");


        //Insert into DB
        Connection connection = getConnection();
        String sql = "insert into flash_sale_user(id,username,password,salt,register_date,login_count) values(?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            FlashSaleUser user = users.get(i);
            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getSalt());
            preparedStatement.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
            preparedStatement.setInt(6, user.getLoginCount());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.clearParameters();
        connection.close();
        System.out.println("insert to db");



        //Log in, generate user ticket
        String urlString = "http://localhost:8083/login/do_login";
        File file = new File("d:\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            FlashSaleUser user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            //co.setDoInput(true);

            OutputStream outputStream = co.getOutputStream();
            System.out.println("mobile:" + user.getId());
            System.out.println("DB password:" + user.getPassword());

            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFormPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();


            BufferedReader reader = new BufferedReader(new InputStreamReader(co.getInputStream()) );
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            String response = results.toString();

/*            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());*/
            JSONObject jo = JSON.parseObject(response);
            String token = jo.getString("data");
            System.out.println("Creat toekn: " + user.getId());


//            ObjectMapper mapper = new ObjectMapper();
//            Result respBean = mapper.readValue(response, Result.class);
//            String userTicket = (String) respBean.getData();
//            System.out.println("create userTicket:" + user.getId());
//            String row = user.getId() + "," + userTicket;
            String row = user.getId() + "," + token;
            raf.setLength(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file :" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    /**
     * Get DB Connection
     *
     * @return
     * @throws Exception
     */
    private static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/flash-sales?useSSL=false&useUnicode=yes&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "flashsales";
        String password = "password123";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
