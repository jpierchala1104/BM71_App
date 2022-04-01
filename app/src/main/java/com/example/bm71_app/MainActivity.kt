package com.example.bm71_app

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.example.bm71_app.fragments.DevicesFragment
import com.example.bm71_app.fragments.TerminalFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    private lateinit var mFrag: TerminalFragment
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val REQUEST_ENABLE_BLUETOOTH = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportFragmentManager.addOnBackStackChangedListener(this)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.fragment, DevicesFragment(), "devices").commitNow()
        else
            onBackStackChanged()

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        val devFrag = supportFragmentManager.findFragmentByTag("devices") as DevicesFragment
        devFrag.startScan()
        startBtn.setOnClickListener{startBtnClick()}
        initBtn.setOnClickListener{initBtnClick()}
        closeBtn.setOnClickListener{closeBtnClick()}
        ctrlBtn.setOnClickListener{ctrlBtnClick()}
    }

    fun onFragReplace(){
        mFrag = supportFragmentManager.findFragmentByTag("terminal") as TerminalFragment
        fragment.visibility = View.GONE
        buttonLayout.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().detach(supportFragmentManager.findFragmentByTag("devices") as DevicesFragment).commit()
    }

    private fun startBtnClick(){
        mFrag.send("0")
    }

    private fun initBtnClick(){
        mFrag.send("1")
    }

    private fun closeBtnClick(){
        mFrag.send("2")
    }

    private fun ctrlBtnClick(){
        mFrag.send("3")
    }

    override fun onBackStackChanged() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (!bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(this, "Bluetooth wyłączony", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Włączenie bluetooth anulowane", Toast.LENGTH_SHORT).show()
            }
        }
    }
}