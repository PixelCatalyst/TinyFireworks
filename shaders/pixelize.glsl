#version 330

in vec2 v_texCoord0;
uniform sampler2D tex0;

uniform int pixelSize;

out vec4 o_color;

void main() {
    o_color = texture(tex0, v_texCoord0);
    if (o_color.a > 0.0) {
        vec2 coord = vec2(
        gl_FragCoord.x - (int(gl_FragCoord.x) % pixelSize) + (pixelSize / 2.0),
        gl_FragCoord.y - (int(gl_FragCoord.y) % pixelSize) + (pixelSize / 2.0));

        ivec2 textureSize = textureSize(tex0, 0).xy;
        o_color = texture(tex0, coord / textureSize);
    }
}
