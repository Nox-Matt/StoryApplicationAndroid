package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.DataDummy
import com.dicoding.picodiploma.loginwithanimation.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setup() {
        storyRepository = Mockito.mock(StoryRepository::class.java, Mockito.RETURNS_DEEP_STUBS)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse().listStory!!.filterNotNull()
        val data: PagingData<ListStoryItem> = PagingData.from(dummyStory)

        Mockito.`when`(storyRepository.getStoriesWithLocation()).thenReturn(StoryResponse(listStory = dummyStory, error = false, message = ""))

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStoryList: List<ListStoryItem> = storyViewModel.storiesWithLocation.value
        val actualStory: PagingData<ListStoryItem> = PagingData.from(actualStoryList)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryItemComparator,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        actualStory.let { differ.submitData(it) }

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Should Not Null`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse().listStory!!.filterNotNull()
        val data: PagingData<ListStoryItem> = PagingData.from(dummyStory)

        Mockito.`when`(storyRepository.getStoriesWithLocation()).thenReturn(StoryResponse(listStory = dummyStory, error = false, message = ""))

        val mainViewModel = StoryViewModel(storyRepository)
        val actualStoryList: List<ListStoryItem> = mainViewModel.storiesWithLocation.value
        val actualStory: PagingData<ListStoryItem> = PagingData.from(actualStoryList)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryItemComparator,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        actualStory.let { differ.submitData(it) }

        assertNotNull(differ.snapshot())
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}