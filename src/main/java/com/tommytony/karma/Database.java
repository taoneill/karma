package com.tommytony.karma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class Database {

	private final Karma karma;

	public Database(Karma karmaPlugin) {
		this.karma = karmaPlugin;
	}
	
	public boolean exists(String playerName) {
		boolean exists = false;
		if (this.sqlite()) {
			try {
	            Connection conn = this.getConnection();
	            Statement stat = conn.createStatement();
	            ResultSet result = stat.executeQuery("select name from players where name='" + playerName + "'");
	            exists = result.next();
	            result.close();
	            stat.close();
	            conn.close();
	        } catch (SQLException e) {
	        	this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Error while checking for existence of " + playerName + ". " + e.toString());
	        }	
		}
		return exists;
	}
	
	public KarmaPlayer get(String playerName) {
		KarmaPlayer karmaPlayer = null;
		if (this.sqlite()) {
			try {
	            Connection conn = this.getConnection();
	            Statement stat = conn.createStatement();
	            ResultSet result = stat.executeQuery("select * from players where name='" + playerName + "'");
	            if (result.next()) {
	            	karmaPlayer = new KarmaPlayer(this.karma, playerName, result.getInt("karma"), result.getLong("lastactive"), result.getLong("lastgift"));
	            }
	            result.close();
	            stat.close();
	            conn.close();
	        } catch (SQLException e) {
	        	this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Error while getting " + playerName + ". " + e.toString());
	        }	
		}
		return karmaPlayer;
	}
	
	public void put(KarmaPlayer karmaPlayer) {
		if (this.sqlite()) {
			boolean exists = this.exists(karmaPlayer.getName());
			try {
	            Connection conn = this.getConnection();
	            Statement stat = conn.createStatement();
	            if (exists) {
	            	// update
	            	stat.executeUpdate(
		            		"update players set karma=" + karmaPlayer.getKarmaPoints() 
		            		+ ", lastactive=" + karmaPlayer.getLastActivityTime()
		            		+ ", lastgift=" + karmaPlayer.getLastGiftTime()
		            		+ " where name='" + karmaPlayer.getName() + "'");
	            } else {
	            	// insert
	            	stat.executeUpdate(
		            		"insert into players values ('" + karmaPlayer.getName() + "', " 
		            		+ karmaPlayer.getKarmaPoints() + ", " + karmaPlayer.getLastActivityTime()+ ", " + karmaPlayer.getLastGiftTime() + ", 0)"); 
	            }
	            stat.close();
	            conn.close();
	        } catch (SQLException e) {
	        	if (exists) {
	        		this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Error while updating " + karmaPlayer.getName() + ". " + e.toString());
	        	} else {
	        		this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Error while inserting " + karmaPlayer.getName() + ". " + e.toString());
	        	}
	        }				
		}
	}
	
	public void putAll() {
		for (String playerName : this.karma.getPlayers().keySet()) {
			KarmaPlayer player = this.karma.getPlayers().get(playerName);
			this.put(player);
		}
	}
	
	public void initialize() {
		if (this.karma.getDataFolder().mkdir()) {
			this.karma.getServer().getLogger().log(Level.INFO, "Karma> Creating files...");
		}
		
		if (this.sqlite()) {
			try {
	            Connection connection = this.getConnection();
	            Statement statement = connection.createStatement();
	            statement.executeUpdate("create table if not exists players (name text, karma numeric, lastactive numeric)");
	            
	            boolean updatedSchema = true;
	            try {
	            	Statement alterStatement = connection.createStatement();
	            	alterStatement.executeUpdate("alter table players add column lastgift numeric");
	            } catch (SQLException e) {
	            	updatedSchema = false;
	            }
	            
	            this.addColumn(connection, "lastgift numeric");
	            this.addColumn(connection, "lastprize numeric");     
	            
	            statement.close();
	            connection.close();
	        } catch (SQLException e) {
	        	this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Error while intilializing database. " + e.toString());
	        }	
		}
	}
	
	private void addColumn(Connection connection, String newColumn) {
		 boolean updatedSchema = true;
         try {
         	Statement alterStatement = connection.createStatement();
         	alterStatement.executeUpdate("alter table players add column " + newColumn);
         } catch (SQLException e) {
         	updatedSchema = false;
         }
         
         if (updatedSchema) {
         	this.karma.getServer().getLogger().log(Level.INFO, "Karma> Table schema updated to add " + newColumn + ".");
         }
	}

	private boolean sqlite() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			this.karma.getServer().getLogger().log(Level.WARNING, "Karma> You need SQLite to run this plugin.");
			return false;
		}
		return true;
	}
	
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:plugins/Karma/karma.db");
	}
}
