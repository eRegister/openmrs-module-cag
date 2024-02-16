//package org.openmrs.module.cag.api.db.hibernate;
//
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.openmrs.Patient;
//import org.openmrs.User;
//import org.openmrs.api.db.hibernate.DbSessionFactory;
//import org.openmrs.module.cag.cag.Cag;
//import org.openmrs.module.cag.cag.CagPatient;
//import org.openmrs.test.BaseModuleContextSensitiveTest;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class HibernateCagDaoTest extends BaseModuleContextSensitiveTest {
//
//	@Autowired
//	HibernateCagDao dao;
//
//	@Autowired
//	Cag cag;
//
//	@Autowired
//	CagPatient cag_patient;
//
//	DbSessionFactory dbSessionFactory;
//
//	@Before
//	public void initialize_Session() {
//		Configuration con = new Configuration().configure().addAnnotatedClass(Cag.class).addAnnotatedClass(CagPatient.class);
//
//		con.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//		con.setProperty("hibernate.cache.use_second_level_cache", "hibernate.cache.use_query_cache");
//		con.setProperty("hibernate.current_session_context_class", "thread");
//		con.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
//		con.setProperty("hibernate.connection.url", "jdbc:mysql://172.17.0.2:3306/openmrs");
//		con.setProperty("hibernate.connection.username", "root");
//		con.setProperty("hibernate.connection.password", "Admin123");
//		con.setProperty("hibernate.show_sql", "true");
//
//		SessionFactory sessionFactory = con.buildSessionFactory();
//
//		dbSessionFactory = new DbSessionFactory(sessionFactory);
//		dao.setSessionFactory(dbSessionFactory);
//	}
//
//	@Test
//	@Ignore("Unignore saveCag_ShouldSaveCag, if you want to make the Item class persistable, see also Item and liquibase.xml")
//	public void saveCag_ShouldSaveCag() {
//
//		User user = new User(1);
//		user.setCreator(user);
//		cag.setCreator(user);
//		cag.setName("Bataung Community ART GROUP");
//		cag.setDescription("Thusanang Bataung Art Group");
//		cag.setVillage("Ha Makhate");
//		cag.setConstituency("Liphiring");
//		cag.setVoided(false);
//		cag.setDistrict("Mohale's Hoek");
//		cag.setUuid("59f900e4-3c38-11ee-be56-0242ac120002");
//
//		dao.saveCag(cag);
//
//		Cag savedCag = (Cag) dao.getCagById(8);
//
//		assertEquals(savedCag.getId(), cag.getId());
//	}
//
//	@Test
//	@Ignore("Unignore updateCag_ShouldUpdateCag, if you want to make the Item class persistable, see also Item and liquibase.xml")
//	public void updateCag_ShouldUpdateCag() {
//
//		User user = new User(1);
//		user.setCreator(user);
//		cag.setCreator(user);
//		cag.setName("Monna Community ART GROUP");
//		cag.setDescription("Pere tse meja Tsa Ha Raboko Holima Borokho");
//		cag.setVillage("Phamong");
//		cag.setConstituency("Takalatsa");
//		cag.setDateChanged(new Date());
//		cag.setVoided(false);
//		cag.setDistrict("Mohale's hoek");
//		cag.setUuid("59f900e4-3c38-11ee-be56-0242ac120002");
//
//		dao.updateCag(cag);
//		Cag updatedCag = (Cag) dao.getCagById(8);
//
//		assertEquals(updatedCag.getDescription(), cag.getDescription());
//	}
//
//	@Test
//	@Ignore("Unignore testTestGetCagByUuid, if you want to make the Item class persistable, see also Item and liquibase.xml")
//	public void getCagById_ShouldGetCag() {
//
//		Cag retrievedCag = dao.getCagById(4);
//		Assert.assertTrue(retrievedCag.getId() == 4);
//	}
//
//	@Test
//	@Ignore("Unignore testTestGetCagByUuid, if you want to make the Item class persistable, see also Item and liquibase.xml")
//	public void getCagBUuid_ShouldGetCagBUuid() {
//
//		Cag retrievedCag = dao.getCagByUuid("59f900e4-3c38-11ee-be56-0242ac120002");
//		assertEquals(retrievedCag.getUuid(), "59f900e4-3c38-11ee-be56-0242ac120002");
//	}
//
//	@Test
//	@Ignore("Unignore testGetCagList_shouldGetCagList, if you want to make the Item class persistable, see also Item and liquibase.xml")
//	public void getCagList_shouldGetCagList() {
//
//		List<Cag> cags = dao.getCagList();
//
//		for (Cag cag : cags)
//			System.out.println(cag);
//
//		Assert.assertTrue("Cag list is not empty", cags.size() > 0);
//	}
//
//	@Test
//	@Ignore("Unignore if you want to make the Item class persistable, see also Item and liquibase.xml")
//	public void saveCagPatient_shouldSaveCagPatient() {
//		Patient patient = new Patient(8706);
//		//		System.out.println(patient);
//
//		cag.setId(1);
//		cag_patient.setStatus(true);
//		cag_patient.setCagPatientId(1);
//		cag_patient.setCagId(cag.getId());
//		cag_patient.setPatientId(patient.getPatientId());
//
//		dao.saveCagPatient(cag_patient);
//
//		System.out.println("getCagPatientId(): " + cag_patient.getCagPatientId());
//		//
//		CagPatient savedCagPatient = dao.getCagPatientById(1);
//		System.out.println(savedCagPatient);
//		//
//		Assert.assertEquals(cag_patient.getCagPatientId(), savedCagPatient.getCagPatientId());
//	}
//
//	@Test
//	@Ignore("Unignore testGetCagPatientList_shouldGetCagPatientList. if you want test getcaglist functionality in CagDao")
//	public void getCagPatientList_shouldGetCagPatientList() {
//
//		int id = 1;
//		List<Patient> cagPatientList = dao.getCagPatientList(id);
//		//
//		//		for (CagPatient p : cagPatient1) {
//		//			System.out.println(p);
//		//		}
//	}
//
//	@Test
//	@Ignore("Unignore deleteCag_shouldDeleteCag, if you want to test delete functionality in CagDao")
//	public void deleteCag_shouldDeleteCag() {
//		String uuid = "59f900e4-3c38-11ee-be56-0242ac120002";
//		dao.deleteCag(uuid);
//	}
//
//}
