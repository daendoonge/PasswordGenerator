package com.example.passwordgenerator;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PasswordDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
      MySQLiteHelper.PASSWORD };

  public PasswordDataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public Password createPassword(String password) {
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.PASSWORD, password);
    long insertId = database.insert(MySQLiteHelper.TABLE_PASSWORD, null,
        values);
    Cursor cursor = database.query(MySQLiteHelper.TABLE_PASSWORD,
        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Password newPassword = cursorToPassword(cursor);
    cursor.close();
    return newPassword;
  }

  public void deletePassword(Password password) {
    long id = password.getID();
    System.out.println("Comment deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_PASSWORD, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }

  public List<Password> getAllPasswords() {
    List<Password> passwords = new ArrayList<Password>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_PASSWORD,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Password password = cursorToPassword(cursor);
      passwords.add(password);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return passwords;
  }

  private Password cursorToPassword(Cursor cursor) {
    Password password = new Password();
    password.setID(cursor.getLong(0));
    password.setPassword(cursor.getString(1));
    return password;
  }
} 