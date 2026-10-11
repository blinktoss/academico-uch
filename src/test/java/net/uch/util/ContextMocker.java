package net.uch.util;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.faces.context.FacesContext;

public abstract class ContextMocker extends FacesContext {

    private static final Release RELEASE = new Release();

    private ContextMocker() {
    }

    public static FacesContext mockFacesContext() {
        FacesContext context = Mockito.mock(FacesContext.class);
        setCurrentInstance(context);

        Mockito.doAnswer(RELEASE)
                .when(context)
                .release();

        return context;
    }

    private static class Release implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) {
            setCurrentInstance(null);
            return null;
        }
    }
}
