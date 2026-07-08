#version 330

in vec2 v_texCoord0;
uniform sampler2D tex0;

uniform float factor = 1.0;

out vec4 o_color;

void main() {
    o_color = texture(tex0, v_texCoord0);
    o_color.a *= factor;
    o_color.a *= step(0.04, o_color.a);
}
