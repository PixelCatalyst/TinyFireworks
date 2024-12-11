#version 330

in vec2 v_texCoord0;
uniform sampler2D tex0;

out vec4 o_color;

void main() {
    vec4 color = texture(tex0, v_texCoord0);
    color.b = 0.0;
    o_color = color;
}
