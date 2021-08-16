package com.example.skeleton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var skeleton: Skeleton
    private lateinit var dailyAdapter : DailyMovieListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dailyAdapter = DailyMovieListAdapter()
        // or apply a new SkeletonLayout to a RecyclerView
        skeleton = rv_main.applySkeleton(R.layout.item_holder)

        rv_main.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyAdapter
            skeleton.showSkeleton()
        }

    }

    override fun onResume() {
        dailyAdapter.setData(KMovieOfficeItem("1","테스트","2020-01-01","야호","2021", "2021","NEW"))
        dailyAdapter.dataNotify()
        skeleton.showOriginal()
        super.onResume()
    }

    class DailyMovieListAdapter : RecyclerView.Adapter<DailyMovieListHolder>(), DailyMovieListHolder.OnItemClick {

        interface OnItemClickListener {
            fun onItemClick(item: KMovieOfficeItem)
        }

        var itemListener: OnItemClickListener? = null

        fun setItemClickListener(listener: OnItemClickListener) {
            this.itemListener = listener
        }

        private var movieList = mutableListOf<KMovieOfficeItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyMovieListHolder {
            var holder = DailyMovieListHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_holder, parent, false)
            )
            return holder
        }

        override fun getItemCount(): Int = movieList.size

        override fun onBindViewHolder(holderDaily: DailyMovieListHolder, position: Int) =
            holderDaily.onBind(movieList[position])

        fun setData(list: KMovieOfficeItem) {
            movieList.add(list)
        }

        fun clearData() {
            movieList = mutableListOf()
        }

        fun dataNotify() {
            notifyDataSetChanged()
        }

        override fun onItemClick(position: Int) {
            itemListener?.onItemClick(movieList[position])
        }
    }

    class DailyMovieListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        interface OnItemClick {
            fun onItemClick(position: Int)
        }

        var onItemListener: OnItemClick?= null

        fun setItemListener(listener: OnItemClick) {
            this.onItemListener = listener
        }
        private val decimalFormat = DecimalFormat("###,###")
        private val vgItem = itemView.findViewById<ViewGroup>(R.id.vg_item)
        private val ivItem = itemView.findViewById<ImageView>(R.id.iv_item)
        private val tvIndex = itemView.findViewById<TextView>(R.id.tv_index)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tv_name)
        private val tvOpenDt = itemView.findViewById<TextView>(R.id.tv_openDt)
        private val tvDirector = itemView.findViewById<TextView>(R.id.tv_director)
        private val rbItem = itemView.findViewById<RatingBar>(R.id.rb_item)
        private val tvRating = itemView.findViewById<TextView>(R.id.tv_rating)

        fun onBind(daily: KMovieOfficeItem) {
            var rating = 0.0
            if(daily.userRating != "") {
                rating = ((daily.userRating).toFloat() / 2).toDouble()
                tvRating.text = daily.userRating
                rbItem.rating = rating.toFloat()
            }
            if(daily.rank != "") tvIndex.text = daily.rank
            if(daily.movieNm!= "") tvTitle.text = daily.movieNm
            if(daily.openDt != "" && daily.audiAcc != "") {
                if(rating <= 0) {
                    tvOpenDt.text = "${daily.openDt} 개봉 예정"
                    rbItem.visibility = View.GONE
                    tvRating.visibility = View.GONE
                }
                else tvOpenDt.text = "${daily.openDt} 개봉 (${decimalFormat.format(Integer.parseInt(daily.audiAcc))}명)"
            }
            if(daily.image != "")
                Glide.with(itemView.context)
                    .load(daily.image)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(ivItem)
            if(daily.director != "") tvDirector.text = "${daily.director.replace("|", " ")}감독"
            vgItem.setOnClickListener {
                onItemListener?.onItemClick(adapterPosition)
            }
        }

    }


}