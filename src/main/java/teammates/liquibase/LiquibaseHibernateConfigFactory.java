package teammates.liquibase;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.BaseEntity;

import liquibase.ext.hibernate.customfactory.CustomMetadataFactory;
import liquibase.ext.hibernate.database.HibernateDatabase;
import liquibase.ext.hibernate.database.connection.HibernateConnection;

/**
 * Provides Hibernate {@link Metadata} for Liquibase diff operations.
 *
 * <p>Mirrors the configuration in {@link HibernateUtil#buildSessionFactory} so that
 * {@code liquibaseDiffChangelog} compares the live database against the same schema
 * representation that Hibernate uses at runtime — including the physical naming strategy
 * that converts PascalCase entity/column names to snake_case.
 */
public class LiquibaseHibernateConfigFactory implements CustomMetadataFactory {

    @Override
    public Metadata getMetadata(HibernateDatabase database, HibernateConnection connection) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
                .build();

        MetadataSources sources = new MetadataSources(registry);
        sources.addPackage("teammates.storage.entity");
        for (Class<? extends BaseEntity> cls : HibernateUtil.ANNOTATED_CLASSES) {
            sources.addAnnotatedClass(cls);
        }

        MetadataBuilder builder = sources.getMetadataBuilder();
        builder.applyPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

        return builder.build();
    }
}
