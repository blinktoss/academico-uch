package net.uch.common;

import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

/**
 * Created for sistema_academico on 24/02/2024.
 *
 * @author Cesardl
 */
public abstract class AbstractBaseMbViewTree {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseMbViewTree.class);

    protected TreeNode<String> arbol;

    public TreeNode<String> getArbol() {
        if (arbol == null) {
            cargarArbol();
        }
        return arbol;
    }

    public void setArbol(TreeNode<String> arbol) {
        this.arbol = arbol;
    }

    public void cargarArbol() {
        LOG.trace("Building tree");
        try {
            String estructura = armarEstructura();
            var estructuraArbol = new ByteArrayInputStream(estructura.getBytes(StandardCharsets.ISO_8859_1));
            var properties = new Properties();
            properties.load(estructuraArbol);
            arbol = new TreeNodeImpl<>();
            agregarNodos(null, arbol, properties);
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        }
    }

    public abstract String armarEstructura();

    protected int verificarIndice(int ultimoDigito, List<Integer> ultimosDigitos) {
        var estado = 0;
        var grupo = 0;
        for (var i = 0; i < ultimosDigitos.size(); i++) {
            if (ultimosDigitos.get(i) == ultimoDigito) {
                estado = 1;
                grupo = i;
                break;
            }
        }
        if (estado == 0) {
            ultimosDigitos.add(ultimoDigito);
            grupo = ultimosDigitos.size() - 1;
        }
        return grupo;
    }

    private void agregarNodos(String path, TreeNode<String> node, Properties properties) {
        var end = false;
        var counter = 1;
        while (!end) {
            String key = path != null ? path + '.' + counter : String.valueOf(counter);
            String value = properties.getProperty(key);
            if (value != null) {
                TreeNodeImpl<String> nodo = new TreeNodeImpl<>();
                nodo.setData(value);
                node.addChild(counter, nodo);
                agregarNodos(key, nodo, properties);
                counter++;
            } else {
                end = true;
            }
        }
    }
}
