package com.pcm.mvvmapidemo

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcm.mvvmapidemo.adapter.UserAdapter
import com.pcm.mvvmapidemo.api.WebAPIServiceFactory
import com.pcm.mvvmapidemo.data.User
import com.pcm.mvvmapidemo.databinding.ActivityMainBinding
import com.pcm.mvvmapidemo.extensions.setToolbar
import com.pcm.mvvmapidemo.extensions.showToast
import com.pcm.mvvmapidemo.listener.OnItemClickListener
import com.pcm.mvvmapidemo.model.UserRepository
import com.pcm.mvvmapidemo.viewmodel.UserViewModel
import com.pcm.mvvmapidemo.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity(), OnItemClickListener<User> {

    lateinit var binding: ActivityMainBinding
    private var userAdapter: UserAdapter? = null

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            UserRepository(
                WebAPIServiceFactory.newInstance().makeServiceFactory()
            )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        setToolbar(binding.toolbar, R.string.app_name)

        userAdapter = UserAdapter(this)
        binding.apply {
            rvUserList.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUserList.adapter = userAdapter
            rvUserList.addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        // hide show progress
        userViewModel.loadingStatus.observe(this, {
            it?.let { binding.progressBar.isVisible = it }
        })

        // show error message
        userViewModel.errorMessage.observe(this, {
            it?.let { showToast(it) }
        })

        // show data
        userViewModel.userList.observe(this, {
            it?.let { userAdapter?.setItems(ArrayList(it)) }
        })

        userViewModel.getUsers()

    }

    override fun onItemClick(view: View?, item: User?, position: Int) {

    }


}
