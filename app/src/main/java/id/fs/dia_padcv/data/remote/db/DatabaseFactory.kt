package id.fs.dia_padcv.data.remote.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://server25.1server.co.uk:3306/padcvfsr_fsrdc"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = "padcvfsr"
            password = "6igW4Vx+5OkG#6"
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }
}