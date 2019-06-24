package com.cccdlabs.sarva.domain.interactors.partners;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestDataComponent;
import com.cccdlabs.sarva.presentation.di.components.TestDataComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class GetActivePartnersUseCaseTest {

    private GetActivePartnersUseCase mUseCase;
    private PartnerRepository mRepositorySpy;
    private List<Partner> mPartnerList;
    private AppDatabase mDb;

    class TestUseCaseObserver extends TestObserver<List<Partner>> {

        private List<Partner> expectedResultList;
        private List<Partner> unexpectedResultList;
        private List<Partner> actualResultList;
        private boolean hasTriggeredOnSuccess;

        TestUseCaseObserver(@NonNull List<Partner> expectedResultList, List<Partner> unexpectedResultList) {
            this.expectedResultList = expectedResultList;
            this.unexpectedResultList = unexpectedResultList == null ? new ArrayList<Partner>(0) : unexpectedResultList;
        }

        @Override
        public void onSuccess(List<Partner> list) {
            actualResultList = list;
            hasTriggeredOnSuccess = true;
        }

        public void assertExpectedPartners() {
            if (!hasTriggeredOnSuccess) {
                throw fail("onSuccess(List<Partner>) not executed");
            }

            // make sure result not null and expected size
            int expected = expectedResultList.size();
            assertNotNull("onSuccess(List<Partner>) null", actualResultList);
            assertEquals("List<Partner> size not " + expected, expected, actualResultList.size());

            for (Partner partner : actualResultList) {
                String uuid = partner.getUuid();

                // assert an unexpected result is not contained in result
                for (Partner p2 : unexpectedResultList) {
                    if (uuid.equals(p2.getUuid())) {
                        throw fail("Inactive Partner[uuid: " + uuid + "] returned in result");
                    }
                }

                // assert result is contained within list of expected results
                boolean isExpected = false;
                for (Partner p2 : expectedResultList) {
                    if (uuid.equals(p2.getUuid())) {
                        isExpected = true;
                        break;
                    }
                }
                if ( ! isExpected) {
                    throw fail("Unexpected Partner result [uuid: " + uuid + "]");
                }
            }
        }

        public void assertPartnerCount(int count) {
            int size = actualResultList.size();
            if (size != count) {
                throw fail("Value counts differ; expected: " + count + " but was: " + size);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();
        mDb = dataComponent.appDatabase();
        PartnerRepository repository = dataComponent.partnerRepository();
        dataComponent.inject(repository);
        mRepositorySpy = spy(repository);
        mUseCase = new GetActivePartnersUseCase(mRepositorySpy);
        mPartnerList = new ArrayList<>();

        Partner partner = new Partner();
        partner.setUuid("d4f74431-2f49-4acb-8f81-57c2ed67047a");
        partner.setActive(true);
        mPartnerList.add(partner);
        mRepositorySpy.insert(partner);

        partner = new Partner();
        partner.setUuid("421b7f06-c3de-4949-869b-fb1ee237c2f6");
        partner.setActive(true);
        mPartnerList.add(partner);
        mRepositorySpy.insert(partner);

        partner = new Partner();
        partner.setUuid("44cf4c02-542c-4f0c-acfd-1bbcce1bc30a");
        partner.setActive(false);
        mPartnerList.add(partner);
        mRepositorySpy.insert(partner);
    }

    @Test
    public void shouldEmitSinglePartnerList() throws Exception {
        GetActivePartnersUseCase useCaseSpy = spy(mUseCase);
        final List<Partner> expectedList = new ArrayList<>(2);
        final List<Partner> unexpectedList = new ArrayList<>(2);
        expectedList.add(mPartnerList.get(0));
        expectedList.add(mPartnerList.get(1));
        unexpectedList.add(mPartnerList.get(2));

        TestUseCaseObserver testObserver = useCaseSpy.execute(null)
                .subscribeWith(new TestUseCaseObserver(expectedList, unexpectedList));

        verify(mRepositorySpy, times(1)).getAllActive();
        verify(useCaseSpy, times(1)).run(null);
        testObserver.assertPartnerCount(2);
        testObserver.assertExpectedPartners();
        testObserver.dispose();
    }

    @Test
    public void shouldEmitSingleWithError() throws Exception {
        GetActivePartnersUseCase useCaseSpy = spy(mUseCase);
        RepositoryException error = new RepositoryException();
        Single<List<Partner>> single = Single.error(error);
        when(useCaseSpy.execute(null)).thenReturn(single);

        single = useCaseSpy.execute(null);
        TestObserver<List<Partner>> testObserver = single.test();
        testObserver.assertError(error);
        testObserver.assertNotComplete();
        testObserver.dispose();
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
        mPartnerList.clear();
        mDb = null;
        mRepositorySpy = null;
        mUseCase = null;
        mPartnerList = null;
    }
}
