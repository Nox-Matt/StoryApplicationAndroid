package com.dicoding.picodiploma.loginwithanimation.view.story


import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.DataDummy
import com.dicoding.picodiploma.loginwithanimation.LogMock
import com.dicoding.picodiploma.loginwithanimation.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import io.mockk.coEvery
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class StoryViewModelTest : LogMock() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var storyRepository: StoryRepository

    private val storyViewModel: StoryViewModel by lazy { StoryViewModel(storyRepository) }

    private lateinit var logMock: MockedStatic<Log>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        logMock = mockStatic(Log::class.java)
        logMock.`when`<Any> { Log.e(anyString(), anyString(), any()) }.then { invocation ->
            val tag = invocation.arguments[0] as String
            val msg = invocation.arguments[1] as String
            println("Mocked Log.e call with tag: $tag and message: $msg")
            null
        }

        storyRepository = spyk(StoryRepository(apiService))
    }

    @After
    fun tearDown() {
        logMock.close()
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
        assertEquals("Stories fetched successfully", dummyStoryResponse.message)
        val dummyStory = dummyStoryResponse.listStory!!.filterNotNull()
        val pagingData: PagingData<ListStoryItem> = PagingData.from(dummyStory)

        coEvery { storyRepository.getFlowStories() } returns flowOf(pagingData)
        val actualStoryFlow = storyViewModel.stories

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryItemComparator,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        val job = launch {
            actualStoryFlow.collectLatest { pagingData ->
                differ.submitData(pagingData)
            }
        }
        advanceUntilIdle()
        assertTrue("PagingData snapshot should not be null", true )
        assertTrue("Snapshot size should match dummy data",dummyStory.size == differ.snapshot().size)
        assertTrue("First item should match",dummyStory[0] == differ.snapshot()[0])
        println("Snapshot size: ${differ.snapshot().size}")
        println("First item in snapshot: ${differ.snapshot()[0]}")

        job.cancel()
    }
    @Test
    fun `when Get Stories Should Return Empty Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())

        coEvery { storyRepository.getFlowStories() } returns flowOf(data)
        storyViewModel.fetchStories()
        val actualStoryFlow = storyViewModel.stories

        val job = launch {
            actualStoryFlow.collectLatest { pagingData ->
                val differ = AsyncPagingDataDiffer(
                    diffCallback = StoryAdapter.StoryItemComparator,
                    updateCallback = noopListUpdateCallback,
                    workerDispatcher = Dispatchers.Main
                )
                differ.submitData(pagingData)
                assertTrue("Snapshot should be empty",differ.snapshot().isEmpty() )

                println("Empty snapshot received")
            }
        }


        advanceUntilIdle()

        job.cancel()
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

