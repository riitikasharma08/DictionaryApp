package com.neatroots.dictionaryapp


import MeaningAdapter
import RetrofitInstance
import WordResult
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.neatroots.dictionaryapp.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter : MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString()
            getMeaning(word)

        }
        adapter =MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager =LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter =adapter
    }
    private  fun getMeaning(word:String){
        setInProgress(true)
        GlobalScope.launch{
            try{
                val response =  RetrofitInstance.dictionaryApi.getMeaning(word)
                if(response.body()==null){
                    throw (Exception())
                }
                runOnUiThread {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
            }} catch (e:Exception){
                runOnUiThread {
                    setInProgress(false)
                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }

           }
        }

    private fun setUI(response : WordResult){
        binding.wordTextview.text =response.word
        binding.phoneticTextview.text=response.phonetic
        adapter.updateNewData(response.meanings)
    }
    private fun setInProgress(inProgress: Boolean){
        if(inProgress){
            binding.searchBtn.visibility= View.INVISIBLE
            binding.progressBar.visibility=View.VISIBLE
        }
        else{
            binding.searchBtn.visibility= View.VISIBLE
            binding.progressBar.visibility=View.INVISIBLE
        }
    }
}



