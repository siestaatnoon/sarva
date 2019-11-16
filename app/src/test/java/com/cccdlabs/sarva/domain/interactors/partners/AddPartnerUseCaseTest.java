package com.cccdlabs.sarva.domain.interactors.partners;

import android.content.Context;

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

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AddPartnerUseCaseTest {

    private static final String TEST_UUID = "d4f74431-2f49-4acb-8f81-57c2ed67047a";

    private AddPartnerUseCase mUseCase;
    private PartnerRepository mRepositorySpy;
    private Partner mPartner;
    private AppDatabase mDb;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();
        mDb = dataComponent.appDatabase();
        PartnerRepository repository = dataComponent.partnerRepository();
        mRepositorySpy = spy(repository);
        mUseCase = new AddPartnerUseCase(mRepositorySpy);

        mPartner = new Partner();
        mPartner.setUuid(TEST_UUID);
    }

    @Test
    public void shouldAddPartner() throws Exception {
        AddPartnerUseCase useCaseSpy = spy(mUseCase);
        TestObserver<Partner> testObserver = useCaseSpy.execute(mPartner).test();

        verify(mRepositorySpy, times(1)).sync(mPartner);
        verify(useCaseSpy, times(1)).run(mPartner);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.dispose();

        Partner partner = mRepositorySpy.getByUuid(TEST_UUID);
        assertNotNull("Partner has not added", partner);
    }

    @Test
    public void shouldEmitSingleWithError() throws Exception {
        AddPartnerUseCase useCaseSpy = spy(mUseCase);
        RepositoryException error = new RepositoryException();
        Single<Partner> single = Single.error(error);
        when(useCaseSpy.execute(mPartner)).thenReturn(single);

        single = useCaseSpy.execute(mPartner);
        TestObserver<Partner> testObserver = single.test();

        testObserver.assertError(error);
        testObserver.assertNotComplete();
        testObserver.dispose();
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
        mDb = null;
        mRepositorySpy = null;
        mUseCase = null;
        mPartner = null;
    }
}
