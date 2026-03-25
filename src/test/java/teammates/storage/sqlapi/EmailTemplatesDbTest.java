package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.query.MutationQuery;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailTemplatesDb}.
 */
public class EmailTemplatesDbTest extends BaseTestCase {

    private EmailTemplatesDb emailTemplatesDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        emailTemplatesDb = spy(EmailTemplatesDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testGetEmailTemplate_templateExists_returnsTemplate() {
        EmailTemplate expectedTemplate = new EmailTemplate("KEY", "Subject", "Body");

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<EmailTemplate> cr = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<EmailTemplate> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        TypedQuery<EmailTemplate> query = mock(TypedQuery.class);
        @SuppressWarnings("unchecked")
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        mockHibernateUtil.when(HibernateUtil::getCriteriaBuilder).thenReturn(cb);
        doReturn(cr).when(cb).createQuery(EmailTemplate.class);
        doReturn(root).when(cr).from(EmailTemplate.class);
        doReturn(path).when(root).get("templateKey");
        doReturn(predicate).when(cb).equal(path, "KEY");
        doReturn(cr).when(cr).select(root);
        doReturn(cr).when(cr).where(predicate);
        mockHibernateUtil.when(() -> HibernateUtil.createQuery(cr)).thenReturn(query);
        doReturn(Stream.of(expectedTemplate)).when(query).getResultStream();

        EmailTemplate actualTemplate = emailTemplatesDb.getEmailTemplate("KEY");

        assertEquals(expectedTemplate, actualTemplate);
    }

    @Test
    public void testGetEmailTemplate_templateDoesNotExist_returnsNull() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<EmailTemplate> cr = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<EmailTemplate> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        TypedQuery<EmailTemplate> query = mock(TypedQuery.class);
        @SuppressWarnings("unchecked")
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        mockHibernateUtil.when(HibernateUtil::getCriteriaBuilder).thenReturn(cb);
        doReturn(cr).when(cb).createQuery(EmailTemplate.class);
        doReturn(root).when(cr).from(EmailTemplate.class);
        doReturn(path).when(root).get("templateKey");
        doReturn(predicate).when(cb).equal(path, "MISSING_KEY");
        doReturn(cr).when(cr).select(root);
        doReturn(cr).when(cr).where(predicate);
        mockHibernateUtil.when(() -> HibernateUtil.createQuery(cr)).thenReturn(query);
        doReturn(Stream.empty()).when(query).getResultStream();

        EmailTemplate actualTemplate = emailTemplatesDb.getEmailTemplate("MISSING_KEY");

        assertNull(actualTemplate);
    }

    @Test
    public void testUpsertEmailTemplate_existingTemplate_updatesAndReturnsFetched()
            throws InvalidParametersException {
        EmailTemplate updatedTemplate = new EmailTemplate("KEY", "New Subject", "New Body");

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaUpdate<EmailTemplate> update = mock(CriteriaUpdate.class);
        @SuppressWarnings("unchecked")
        Root<EmailTemplate> root = mock(Root.class);
        MutationQuery mockQuery = mock(MutationQuery.class);

        mockHibernateUtil.when(HibernateUtil::getCriteriaBuilder).thenReturn(cb);
        doReturn(update).when(cb).createCriteriaUpdate(EmailTemplate.class);
        doReturn(root).when(update).from(EmailTemplate.class);
        mockHibernateUtil.when(() -> HibernateUtil.createMutationQuery(update)).thenReturn(mockQuery);
        doReturn(1).when(mockQuery).executeUpdate();
        doReturn(updatedTemplate).when(emailTemplatesDb).getEmailTemplate("KEY");

        EmailTemplate result = emailTemplatesDb.upsertEmailTemplate(updatedTemplate);

        verify(mockQuery, times(1)).executeUpdate();
        mockHibernateUtil.verify(() -> HibernateUtil.persist(any(EmailTemplate.class)), never());
        assertEquals("New Subject", result.getSubject());
        assertEquals("New Body", result.getBody());
    }

    @Test
    public void testUpsertEmailTemplate_newTemplate_persistsWhenNoRowUpdated()
            throws InvalidParametersException {
        EmailTemplate newTemplate = new EmailTemplate("KEY", "Subject", "Body");

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaUpdate<EmailTemplate> update = mock(CriteriaUpdate.class);
        @SuppressWarnings("unchecked")
        Root<EmailTemplate> root = mock(Root.class);
        MutationQuery mockQuery = mock(MutationQuery.class);

        mockHibernateUtil.when(HibernateUtil::getCriteriaBuilder).thenReturn(cb);
        doReturn(update).when(cb).createCriteriaUpdate(EmailTemplate.class);
        doReturn(root).when(update).from(EmailTemplate.class);
        mockHibernateUtil.when(() -> HibernateUtil.createMutationQuery(update)).thenReturn(mockQuery);
        doReturn(0).when(mockQuery).executeUpdate();
        doReturn(newTemplate).when(emailTemplatesDb).getEmailTemplate("KEY");

        EmailTemplate result = emailTemplatesDb.upsertEmailTemplate(newTemplate);

        verify(mockQuery, times(1)).executeUpdate();
        mockHibernateUtil.verify(() -> HibernateUtil.persist(newTemplate));
        assertEquals(newTemplate, result);
    }

    @Test
    public void testUpsertEmailTemplate_invalidTemplate_throwsInvalidParametersException() {
        EmailTemplate invalidTemplate = new EmailTemplate("KEY", "", "Body");

        assertThrows(InvalidParametersException.class,
                () -> emailTemplatesDb.upsertEmailTemplate(invalidTemplate));

        mockHibernateUtil.verify(() -> HibernateUtil.getCriteriaBuilder(), never());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(any(EmailTemplate.class)), never());
    }

    @Test
    public void testDeleteEmailTemplate_byKey_executesDelete() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaDelete<EmailTemplate> cd = mock(CriteriaDelete.class);
        @SuppressWarnings("unchecked")
        Root<EmailTemplate> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        MutationQuery mutationQuery = mock(MutationQuery.class);

        mockHibernateUtil.when(HibernateUtil::getCriteriaBuilder).thenReturn(cb);
        doReturn(cd).when(cb).createCriteriaDelete(EmailTemplate.class);
        doReturn(root).when(cd).from(EmailTemplate.class);
        doReturn(path).when(root).get("templateKey");
        doReturn(predicate).when(cb).equal(path, "KEY");
        doReturn(cd).when(cd).where(predicate);
        mockHibernateUtil.when(() -> HibernateUtil.createMutationQuery(cd)).thenReturn(mutationQuery);

        emailTemplatesDb.deleteEmailTemplate("KEY");

        mockHibernateUtil.verify(() -> HibernateUtil.createMutationQuery(cd));
        verify(mutationQuery, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteEmailTemplate_templateExists_removeCalled() {
        EmailTemplate template = new EmailTemplate("KEY", "Subject", "Body");

        emailTemplatesDb.deleteEmailTemplate(template);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(template));
    }

    @Test
    public void testDeleteEmailTemplate_nullTemplate_removeNotCalled() {
        emailTemplatesDb.deleteEmailTemplate((EmailTemplate) null);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }
}
