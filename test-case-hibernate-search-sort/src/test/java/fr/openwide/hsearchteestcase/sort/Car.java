package fr.openwide.hsearchteestcase.sort;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.miscellaneous.TrimFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import fr.openwide.hsearchteestcase.sort.util.HibernateSearchAnalyzer;

@Entity
@Indexed
@AnalyzerDefs({
	@AnalyzerDef(name = HibernateSearchAnalyzer.TEXT_SORT,
			tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),
			filters = {
					@TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
					@TokenFilterDef(factory = LowerCaseFilterFactory.class),
					@TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
						@org.hibernate.search.annotations.Parameter(name = "pattern", value = "('-&\\.,\\(\\))"),
						@org.hibernate.search.annotations.Parameter(name = "replacement", value = " "),
						@org.hibernate.search.annotations.Parameter(name = "replace", value = "all")
					}),
					@TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
						@org.hibernate.search.annotations.Parameter(name = "pattern", value = "([^0-9\\p{L} ])"),
						@org.hibernate.search.annotations.Parameter(name = "replacement", value = ""),
						@org.hibernate.search.annotations.Parameter(name = "replace", value = "all")
					}),
					@TokenFilterDef(factory = TrimFilterFactory.class)
			}
	)
})
public class Car {

	public static final String NAME_SORT = "nameSort";

	@Id
	@DocumentId
	@GeneratedValue
//	@GeneratedValue(generator = GeneratorName.GENERATOR_NAME)
	private Long id;

	@Basic(optional = false)
	@Field(name = NAME_SORT, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT))
	@SortableField(forField = NAME_SORT)
	private String name;

	public Car(String name) {
		super();
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(id) + ", " + String.valueOf(name) + ">";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Car) {
			return ((Car) obj).getId() == getId();
		}
		return super.equals(obj);
	}

}
