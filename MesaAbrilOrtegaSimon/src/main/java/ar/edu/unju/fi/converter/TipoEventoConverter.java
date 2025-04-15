package ar.edu.unju.fi.converter;

import ar.edu.unju.fi.model.TipoEvento;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TipoEventoConverter implements Converter<String, TipoEvento> {
    @Override
    public TipoEvento convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return TipoEvento.valueOf(source);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
