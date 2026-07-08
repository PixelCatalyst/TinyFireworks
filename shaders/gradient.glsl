#version 330

in vec2 v_texCoord0;

uniform vec3 colorTop = vec3(0.0, 0.0, 0.0);
uniform vec3 colorBottom = vec3(0.0, 0.0, 0.0);

out vec4 o_color;

void main() {
    o_color = vec4(mix(colorBottom, colorTop, v_texCoord0.y), 1.0);
}
