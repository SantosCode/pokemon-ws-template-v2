package br.com.heiderlopes.pokemonwstemplatev2.presentation.formpokemon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AbsSeekBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import br.com.heiderlopes.pokemonwstemplatev2.R
import br.com.heiderlopes.pokemonwstemplatev2.databinding.ActivityFormPokemonBinding
import br.com.heiderlopes.pokemonwstemplatev2.domain.model.Pokemon
import br.com.heiderlopes.pokemonwstemplatev2.presentation.model.ViewState
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class FormPokemonActivity : AppCompatActivity(R.layout.activity_form_pokemon) {

    private val formPokemonViewModel: FormPokemonViewModel by viewModel()
    val picasso: Picasso by inject()
    private lateinit var pokemon: Pokemon

    private val viewBinding by lazy {
        ActivityFormPokemonBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        val pokemonNumber = intent.getStringExtra("POKEMON") ?: ""
        formPokemonViewModel.getPokemon(pokemonNumber)

        viewBinding.btSaveForm.setOnClickListener {
            pokemon.attack = viewBinding.sbAttack.progress
            pokemon.defense = viewBinding.sbDefense.progress
            pokemon.velocity = viewBinding.sbPS.progress
            formPokemonViewModel.update(
                pokemon
            )
        }
        registerObserve()
    }

    private fun registerObserve() {
        formPokemonViewModel.pokemonResult.observe(this, {
            when(it) {
                is ViewState.Success -> setValues(it.data)
                is ViewState.Loading -> {}
                is ViewState.Failure -> Toast.makeText(
                    this, it.throwable.message, Toast.LENGTH_LONG
                ).show()
            }
        })
        formPokemonViewModel.pokemonUpdateResult.observe(this, {
            when(it) {
                is ViewState.Success -> Toast.makeText(
                        this, getString(R.string.pokemon_update_sucess), Toast.LENGTH_LONG
                    ).show()
                is ViewState.Loading -> {}
                is ViewState.Failure -> Toast.makeText(
                    this, it.throwable.message, Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setValues(pokemon: Pokemon) {
        this.pokemon = pokemon
        viewBinding.tvPokemonNameForm.text = pokemon.name

        picasso.load("https://pokedexdx.herokuapp.com${pokemon.imageURL}")
            .into(viewBinding.ivPokemonForm)

        viewBinding.sbAttack.progress = pokemon.attack
        viewBinding.sbDefense.progress = pokemon.defense
        viewBinding.sbPS.progress = pokemon.ps
        viewBinding.sbVelocity.progress = pokemon.velocity

        viewBinding.tvAttack.text = pokemon.attack.toString()
        viewBinding.tvDefense.text = pokemon.defense.toString()
        viewBinding.tvPS.text = pokemon.ps.toString()
        viewBinding.tvVelocity.text = pokemon.velocity.toString()

        setListener(viewBinding.sbAttack, viewBinding.tvAttackValue)
        setListener(viewBinding.sbDefense, viewBinding.tvDefenseValue)
        setListener(viewBinding.sbPS, viewBinding.tvPSValue)
        setListener(viewBinding.sbVelocity, viewBinding.tvVelocityValue)
    }

    private fun setListener(seekBar: SeekBar, textView: TextView) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}