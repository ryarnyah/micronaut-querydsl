package com.github.ryarnyah

import com.github.ryarnyah.querydsl.Department
import com.github.ryarnyah.querydsl.QDepartment
import com.github.ryarnyah.querydsl.QTestData
import com.github.ryarnyah.querydsl.TestData
import com.github.ryarnyah.querydsl.configuration.MicronautSQLQueryFactory
import io.micronaut.context.DefaultApplicationContext
import io.micronaut.context.env.MapPropertySource
import io.micronaut.transaction.exceptions.NoTransactionException
import io.micronaut.transaction.jdbc.DataSourceTransactionManager
import io.micronaut.transaction.support.TransactionSynchronizationManager
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.sql.DataSource
import javax.transaction.Transactional

class MicronautQueryDSLTest {

    private val setupDatabase = """
        DROP TABLE IF EXISTS TEST_DATA;
        DROP TABLE IF EXISTS EMPLOYEE;
        DROP TABLE IF EXISTS DEPARTMENT;
        
        CREATE TABLE TEST_DATA(
            UID VARCHAR(256) PRIMARY KEY
        );
        
        CREATE TABLE DEPARTMENT (
          ID IDENTITY PRIMARY KEY,
          NAME VARCHAR(64) NOT NULL
        );

        CREATE TABLE EMPLOYEE (
          ID IDENTITY PRIMARY KEY,
          NAME VARCHAR(64) NOT NULL,
          DEPT_ID LONG,
          CONSTRAINT FK_EMP_DEPT_ID FOREIGN KEY(DEPT_ID) REFERENCES DEPARTMENT(ID)
        );
    """.trimIndent()

    @Test
    fun testNoConfiguration() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.start()

        try {
            Assertions.assertFalse(applicationContext.containsBean(MicronautSQLQueryFactory::class.java))
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testWithDatabaseConfiguration() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to "",
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(MicronautSQLQueryFactory::class.java))
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testSaveData() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to "",
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(DataSource::class.java))
            Assertions.assertTrue(applicationContext.containsBean(DataSourceTransactionManager::class.java))

            val transactionManager = applicationContext.getBean(DataSourceTransactionManager::class.java)

            val dataSource = applicationContext.getBean(DataSource::class.java)
            transactionManager.executeWrite {
                dataSource.connection.use { conn ->
                    conn.createStatement().use { statement ->
                        statement.execute(setupDatabase)
                    }
                }
            }
            Assertions.assertTrue(applicationContext.containsBean(TestService::class.java))
            val testService = applicationContext.getBean(TestService::class.java)
            testService.save(buildTestData("Hello World"))
            Assertions.assertNotNull(testService.findById("Hello World"))

            val testServiceJava = applicationContext.getBean(TestServiceJava::class.java)
            Assertions.assertNotNull(testServiceJava.findById("Hello World"))

            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive())
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testSaveDataTransactional() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to ""
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(DataSource::class.java))
            Assertions.assertTrue(applicationContext.containsBean(DataSourceTransactionManager::class.java))

            val transactionManager = applicationContext.getBean(DataSourceTransactionManager::class.java)

            val dataSource = applicationContext.getBean(DataSource::class.java)
            transactionManager.executeWrite {
                dataSource.connection.use { conn ->
                    conn.createStatement().use { statement ->
                        statement.execute(setupDatabase)
                    }
                }
            }
            Assertions.assertTrue(applicationContext.containsBean(TestService::class.java))
            val testService = applicationContext.getBean(TestService::class.java)

            Assertions.assertThrows(RuntimeException::class.java) {
                testService.saveWithException(buildTestData("test"))
            }
            Assertions.assertNull(testService.findById("test"))
            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive())
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testSaveStreamData() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to ""
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(DataSource::class.java))
            Assertions.assertTrue(applicationContext.containsBean(DataSourceTransactionManager::class.java))

            val transactionManager = applicationContext.getBean(DataSourceTransactionManager::class.java)

            val dataSource = applicationContext.getBean(DataSource::class.java)
            transactionManager.executeWrite {
                dataSource.connection.use { conn ->
                    conn.createStatement().use { statement ->
                        statement.execute(setupDatabase)
                    }
                }
            }
            Assertions.assertTrue(applicationContext.containsBean(TestService::class.java))
            val testService = applicationContext.getBean(TestService::class.java)

            val entities = setOf(buildTestData("one"), buildTestData("two"))
            val resultEntities = testService.saveReadStream(entities)

            entities.forEach { expected ->
                Assertions.assertNotNull(resultEntities.find { it.uid == expected.uid })
            }
            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive())
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testSaveStreamDataNonTransactional() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to ""
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(DataSource::class.java))
            Assertions.assertTrue(applicationContext.containsBean(DataSourceTransactionManager::class.java))

            val transactionManager = applicationContext.getBean(DataSourceTransactionManager::class.java)

            val dataSource = applicationContext.getBean(DataSource::class.java)
            transactionManager.executeWrite {
                dataSource.connection.use { conn ->
                    conn.createStatement().use { statement ->
                        statement.execute(setupDatabase)
                    }
                }
            }
            Assertions.assertTrue(applicationContext.containsBean(TestService::class.java))
            val testService = applicationContext.getBean(TestService::class.java)

            val entities = setOf(buildTestData("one"), buildTestData("two"))
            entities.forEach { testData ->
                testService.save(testData)
            }
            val resultEntities = testService.readAllStream()

            entities.forEach { expected ->
                Assertions.assertNotNull(resultEntities.find { it.uid == expected.uid })
            }
            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive())
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testSaveDataWithoutTransaction() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to ""
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(DataSource::class.java))
            Assertions.assertTrue(applicationContext.containsBean(DataSourceTransactionManager::class.java))

            val transactionManager = applicationContext.getBean(DataSourceTransactionManager::class.java)

            val dataSource = applicationContext.getBean(DataSource::class.java)
            transactionManager.executeWrite {
                dataSource.connection.use { conn ->
                    conn.createStatement().use { statement ->
                        statement.execute(setupDatabase)
                    }
                }
            }
            Assertions.assertTrue(applicationContext.containsBean(TestService::class.java))
            val testService = applicationContext.getBean(TestService::class.java)

            Assertions.assertNull(testService.findById("Hello World"))
            Assertions.assertThrows(NoTransactionException::class.java) {
                testService.saveWithoutTransaction(buildTestData("Hello World"))
            }
            Assertions.assertNull(testService.findById("Hello World"))
            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive())
        } finally {
            applicationContext.stop()
        }
    }

    @Test
    fun testSaveMerge() {
        val applicationContext = DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(
            MapPropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "datasources.default.username" to "sa",
                    "datasources.default.password" to ""
                )
            )
        )
        applicationContext.start()

        try {
            Assertions.assertTrue(applicationContext.containsBean(DataSource::class.java))
            Assertions.assertTrue(applicationContext.containsBean(DataSourceTransactionManager::class.java))

            val transactionManager = applicationContext.getBean(DataSourceTransactionManager::class.java)

            val dataSource = applicationContext.getBean(DataSource::class.java)
            transactionManager.executeWrite {
                dataSource.connection.use { conn ->
                    conn.createStatement().use { statement ->
                        statement.execute(setupDatabase)
                    }
                }
            }
            Assertions.assertTrue(applicationContext.containsBean(TestService::class.java))
            val testService = applicationContext.getBean(TestService::class.java)

            val department = Department()
            department.id = 1
            department.name = "test"
            testService.saveDepartment(department)
            Assertions.assertEquals(testService.findDepartmentById(1).name, "test")

            department.name = "toto"
            testService.saveDepartment(department)
            Assertions.assertEquals(testService.findDepartmentById(1).name, "toto")

            Assertions.assertEquals(testService.countDepartementById(1), 1)
            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive())
        } finally {
            applicationContext.stop()
        }
    }

    private fun buildTestData(id: String): TestData {
        val testData = TestData()
        testData.uid = id
        return testData
    }
}

@Singleton
open class TestService(
    private val sqlQueryFactory: MicronautSQLQueryFactory
) {

    open fun countDepartementById(id: Long): Long {
        return sqlQueryFactory.select(QDepartment.department.id.count())
            .from(QDepartment.department)
            .where(QDepartment.department.id.eq(id))
            .fetchFirst()
    }


    open fun findDepartmentById(id: Long): Department {
        return sqlQueryFactory.selectFrom(QDepartment.department)
            .where(QDepartment.department.id.eq(id))
            .fetchFirst()
    }

    @Transactional
    open fun saveDepartment(entity: Department) {
        sqlQueryFactory.merge(QDepartment.department).populate(entity).execute()
    }

    @Transactional
    open fun save(entity: TestData) {
        sqlQueryFactory.merge(QTestData.testData).populate(entity).execute()
    }

    @Transactional
    open fun saveWithException(entity: TestData) {
        sqlQueryFactory.merge(QTestData.testData).populate(entity).execute()
        sqlQueryFactory.selectFrom(QTestData.testData)
            .where(QTestData.testData.uid.eq(entity.uid))
            .fetch()
        throw RuntimeException("test")
    }

    @Transactional
    open fun saveReadStream(entities: Set<TestData>): Set<TestData> {
        val clause = sqlQueryFactory.merge(QTestData.testData)
        entities.forEach { entity ->
            clause.populate(entity).addBatch()
        }
        clause.execute()
        return readAllStream()
    }

    open fun readAllStream(): Set<TestData> {
        val result = mutableSetOf<TestData>()
        sqlQueryFactory.selectFrom(QTestData.testData)
            .iterate().use { iterator ->
                iterator.forEach {
                    result += it
                }
            }
        return result
    }

    open fun saveWithoutTransaction(entity: TestData) {
        sqlQueryFactory.insert(QTestData.testData).populate(entity).execute()
    }

    open fun findById(id: String): TestData? {
        return sqlQueryFactory.selectFrom(QTestData.testData)
            .where(QTestData.testData.uid.eq(id))
            .fetchFirst()
    }
}