package com.iberdrola.clientes.presentation.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.iberdrola.clientes.R;
import com.iberdrola.clientes.common.constants.GlobalConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Fdez Esteban
 * Ejemplo completo en: @string/condiciones_generales_contratacion_pys o en FragmentSmartHome mostrarDialogo()
 */

public final class SpannableUtils {

    private SpannableUtils() {
        throw new IllegalStateException(GlobalConstants.TEXTO_UTILITY_CLASS);
    }

    /**
     * @param activity     activity
     * @param codigoAccion Example: "http://www.google.es"
     * @param textoEditar  Hola, haz click [aqui].
     * @return Hola, haz click aquí. Siendo "aquí" clickable y en verde.
     */
    public static final SpannableStringBuilder getSpannableStringBuilderSoloHtml(@NonNull final Activity activity, @NonNull final String codigoAccion, @NonNull String textoEditar) {
        List<String> codigoAcciones = new ArrayList<>();
        codigoAcciones.add(codigoAccion);
        return getSpannableStringBuilderSoloHtml(activity, codigoAcciones, textoEditar, null);
    }

    /**
     * @param activity     activity
     * @param codigoAccion Example: "http://www.google.es", "http://www.nasa.gov";
     * @param textoEditar  Hola, haz click [aqui] o en la [Nasa].
     * @return Hola, haz click aquí o en la Nasa. Siendo "aquí" y "Nasa" clickable y en verde.
     */
    public static final SpannableStringBuilder getSpannableStringBuilderSoloHtml(@NonNull final Activity activity, @NonNull final List<String> codigoAccion, @NonNull String textoEditar) {
        return getSpannableStringBuilderSoloHtml(activity, codigoAccion, textoEditar, null);
    }

    /**
     * @param activity     activity
     * @param codigoAccion Example: "http://www.google.es", "http://www.nasa.gov";
     * @param textoEditar  Hola, haz click [aqui] o en la [Nasa].
     * @param colores      Null para color por defecto verde, Solo 1 elemento tipo -> R.color.azul para todos azules, o tantos colores como codigoAccion hay.
     * @return Hola, haz click aquí o en la Nasa. Siendo "aquí" y "Nasa" clickable y en verde o azul o en verde y azul.
     */
    public static final SpannableStringBuilder getSpannableStringBuilderSoloHtml(@NonNull final Activity activity, @NonNull final List<String> codigoAccion, @NonNull String textoEditar, @Nullable final List<Integer> colores) {
        List<ClickableSpan> linkClickable = new ArrayList<>();

        for (int i = 0; i < codigoAccion.size(); ++i) {
            //En este caso estamos ante un Link
            String tempCodigo = codigoAccion.get(i);
            linkClickable.add(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = null;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tempCodigo));
                    activity.startActivity(intent);
                }
            });
        }

        return getSpannableStringBuilderGenerico(activity, linkClickable, textoEditar, colores);
    }

    /**
     * @param activity     activity
     * @param codigoAccion Example: ClickableSpan(PDF, HTML...)...;
     * @param textoEditar  Hola, haz click [aqui] o en la [Nasa].
     * @param linkColores  Null para color por defecto verde, Solo 1 elemento tipo -> R.color.azul para todos azules, o tantos colores como codigoAccion hay.
     * @return Hola, haz click aquí o en la Nasa. Siendo "aquí" y "Nasa" clickable y en verde o azul o en verde y azul.
     */
    public static final SpannableStringBuilder getSpannableStringBuilderGenerico(@NonNull final Activity activity, @NonNull List<ClickableSpan> codigoAccion, @NonNull String textoEditar, @Nullable final List<Integer> linkColores) {
        List<Integer> startLinks = new ArrayList<>();
        List<Integer> endLinks = new ArrayList<>();
        List<ClickableSpan> linkClick = new ArrayList<>();

        //Quito los parentesis, cojo los indices donde se encuentra el Link y añado el evento Click
        while (textoEditar != null && textoEditar.contains("[") && textoEditar.contains("]") && !codigoAccion.isEmpty()) {
            //Primer indice
            startLinks.add(textoEditar.indexOf("["));
            textoEditar = textoEditar.replaceFirst("\\[", "");

            //Segundo indice
            endLinks.add(textoEditar.indexOf("]"));
            textoEditar = textoEditar.replaceFirst("]", "");

            //Evento click. Añado y lo borro de la lista codigoAccion
            linkClick.add(codigoAccion.get(0));
            codigoAccion.remove(0);
        }

        //Meto el textoEditar ya editado en el Spannable y empiezo a meterle los clicks e índices
        return setSpansWithForegorundColorSpan(activity, textoEditar, linkClick, startLinks, endLinks, linkColores);
    }

    /////////////////// MËTODOS PRIVADOS ////////////////////

    private static final SpannableStringBuilder setSpansWithForegorundColorSpan(@NonNull final Activity activity, @NonNull final String textoEditar, @NonNull final List<ClickableSpan> linkClick, @NonNull final List<Integer> startLinks, @NonNull final List<Integer> endLinks, @Nullable final List<Integer> linkColores) {
        SpannableStringBuilder result = new SpannableStringBuilder(textoEditar);

        for (int i = 0; i < startLinks.size(); ++i) {
            result.setSpan(linkClick.get(i), startLinks.get(i), endLinks.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (linkColores == null) {
                result.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.verde)), startLinks.get(i), endLinks.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (linkColores.size() > 1) {
                result.setSpan(new ForegroundColorSpan(activity.getResources().getColor(linkColores.get(i))), startLinks.get(i), endLinks.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (linkColores.size() == 1) {
                result.setSpan(new ForegroundColorSpan(activity.getResources().getColor(linkColores.get(0))), startLinks.get(i), endLinks.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return result;
    }
}
