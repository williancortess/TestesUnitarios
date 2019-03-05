package br.com.alura.leilao.ui.recyclerview.adapter;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.alura.leilao.model.Leilao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListaLeilaoAdapterTest {
    @Mock
    Context context;

    @Spy
    ListaLeilaoAdapter listaLeilaoAdapter = new ListaLeilaoAdapter(context);

    @Test
    public void deve_AtualizarListaDeLeiloes_QuandoReceberListaDeLeiloes() {
//        Context context = Mockito.mock(Context.class);
//        ListaLeilaoAdapter listaLeilaoAdapter = Mockito.spy(new ListaLeilaoAdapter(context));
//        MockitoAnnotations.initMocks(this);

        doNothing().when(listaLeilaoAdapter).atualizaLista();

        listaLeilaoAdapter.atualiza(new ArrayList<>(Arrays.asList(
                new Leilao("Console"),
                new Leilao("Computador")
        )));
        int quantidadeLeiloesDevolvida = listaLeilaoAdapter.getItemCount();

        verify(listaLeilaoAdapter).atualizaLista();
        assertThat(quantidadeLeiloesDevolvida, is(2));
    }

}