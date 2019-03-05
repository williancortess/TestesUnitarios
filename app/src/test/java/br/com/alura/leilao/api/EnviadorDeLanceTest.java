package br.com.alura.leilao.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import br.com.alura.leilao.api.retrofit.client.LeilaoWebClient;
import br.com.alura.leilao.api.retrofit.client.RespostaListener;
import br.com.alura.leilao.exception.LanceMenorQueUltimoLanceException;
import br.com.alura.leilao.exception.LanceSeguidoDoMesmoUsuarioException;
import br.com.alura.leilao.exception.UsuarioJaDeuCincoLancesException;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.ui.dialog.AvisoDialogManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EnviadorDeLanceTest {

    @Mock
    private LeilaoWebClient cliente;
    @Mock
    private EnviadorDeLance.LanceProcessadoListener listener;
    @Mock
    private AvisoDialogManager manager;
    @Mock
    private Leilao leilao;
    @Mock
    private LeilaoWebClient client;

    @Test
    public void  deve_MostrarMensagemDeFalha_QuandoLanceForMenorQueUltimoLance(){
        EnviadorDeLance enviador = new EnviadorDeLance(
                cliente,
                listener,
                manager);

//        Leilao computador = new Leilao("Computador");
//        computador.propoe(new Lance(new Usuario("Will"), 200));

        //Chama exception
        doThrow(LanceMenorQueUltimoLanceException.class)
                .when(leilao).propoe(any(Lance.class));

        enviador.envia(leilao, new Lance(new Usuario("Hele"), 100));

        verify(manager).mostraAvisoLanceMenorQueUltimoLance();
    }

    @Test
    public void  deve_MostrarMensagemDeFalha_QuandoUsuarioComCincoLancesDerNovoLance(){
        EnviadorDeLance enviador = new EnviadorDeLance(
                cliente,
                listener,
                manager);

        //Chama exception
        doThrow(UsuarioJaDeuCincoLancesException.class)
                .when(leilao).propoe(any(Lance.class));

        enviador.envia(leilao, new Lance(new Usuario("Hele"), 100));

        verify(manager).mostraAvisoUsuarioJaDeuCincoLances();
    }

    @Test
    public void deve_MostrarMensagemDeFalha_QuandoOUsuarioDoUltimoLanceDerNovoLance() {
        EnviadorDeLance enviador = new EnviadorDeLance(
                client,
                listener,
                manager);
        doThrow(LanceSeguidoDoMesmoUsuarioException.class)
                .when(leilao).propoe(any(Lance.class));

        enviador.envia(leilao, new Lance(new Usuario("Alex"), 200));

        verify(manager).mostraAvisoLanceSeguidoDoMesmoUsuario();
        verify(client, never()).propoe(any(Lance.class), anyLong(), any(RespostaListener.class));
    }

    @Test
    public void deve_MostraMensagemDeFalha_QuandoFalharEnvioDeLanceParaAPI() {
        EnviadorDeLance enviador = new EnviadorDeLance(
                client,
                listener,
                manager);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                RespostaListener<Void> argument = invocation.getArgument(2);
                argument.falha("");
                return null;
            }
        }).when(client)
                .propoe(any(Lance.class),
                        anyLong(),
                        any(RespostaListener.class));

        enviador.envia(new Leilao("Computador"),
                new Lance(new Usuario("Alex"), 200));

        verify(manager).mostraToastFalhaNoEnvio();
        verify(listener, never()).processado(new Leilao("Computador"));
    }

    @Test
    public void deve_NotificarLanceProcessado_QuandoEnviarLanceParaAPIComSucesso() {
        EnviadorDeLance enviador = new EnviadorDeLance(
                client,
                listener,
                manager);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                RespostaListener<Void> argument = invocation.getArgument(2);
                argument.sucesso(any(Void.class));
                return null;
            }
        }).when(client)
                .propoe(any(Lance.class), anyLong(), any(RespostaListener.class));

        enviador.envia(new Leilao("Computador"),
                new Lance(new Usuario("Alex"), 200));

        verify(listener).processado(any(Leilao.class));
        verify(manager, never()).mostraToastFalhaNoEnvio();
    }
}