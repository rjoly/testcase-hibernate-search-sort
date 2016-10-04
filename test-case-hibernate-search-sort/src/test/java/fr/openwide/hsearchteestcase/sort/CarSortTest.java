package fr.openwide.hsearchteestcase.sort;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.test.SearchTestBase;
import org.hibernate.search.testsupport.TestForIssue;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class CarSortTest extends SearchTestBase {

	public Class<?>[] getAnnotatedClasses() {
		return new Class<?>[]{ Car.class };
	}
	
	@Test
	@TestForIssue(jiraKey = "HSEARCH-2376") // Please fill in the JIRA key of your issue
	@SuppressWarnings("unchecked")
	public void testCarSort() {
		System.out.println("Sort my cars - Test begin");
		Session session = openSession();
		
		Car car1 = new Car("alfa romeo");
		Car car2 = new Car("Mercedes");
		Car car3 = new Car("AUDI");
		Car car4 = new Car("épilogue");
		Car car5 = new Car("Range Rover");
		Car car6 = new Car("Àtester");
		
		Transaction tx = session.beginTransaction();
		session.persist(car1);
		session.persist(car2);
		session.persist(car3);
		session.persist(car4);
		session.persist(car5);
		session.persist(car6);
		tx.commit();
		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Car.class).get();
		Query query = queryBuilder.all().createQuery();
		
		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query);
		
		fullTextQuery.setSort(new Sort(new SortField(Car.NAME_SORT, Type.STRING, false)));
		
		List<Car> result = (List<Car>) fullTextQuery.list();
		System.out.println(result);
		Object[] resultId = Lists.<Long>newArrayList(Iterables.transform(result, new Function<Car, Long>() {
			public Long apply(Car input) {
				return input != null ? input.getId() : null;
			}
		})).toArray();
		
		// Current result
		assertArrayEquals(
				// [<3, AUDI>, <2, Mercedes>, <5, Range Rover>, <1, alfa romeo>, <6, Àtester>, <4, épilogue>]
				Lists.newArrayList(3L, 2L, 5L, 1L, 6L, 4L).toArray(),
				resultId
		);
//		// Result I should have with a case insensitive sort
//		assertArrayEquals(
//				// [<1, alfa romeo>, <6, Àtester>, <3, AUDI>, <4, épilogue>, <2, Mercedes>, <5, Range Rover>]
//				Lists.newArrayList(1L, 6L, 3L, 4L, 2L, 5L).toArray(),
//				resultId
//		);
		
		session.close();
		System.out.println("Sort my cars - Test end");
	}

}
