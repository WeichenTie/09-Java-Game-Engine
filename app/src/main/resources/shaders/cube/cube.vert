#version 400 core
layout (location = 0)in vec3 a_Position;

out vec4 v_Color;

uniform mat4 u_Projection;
uniform mat4 u_Model;
uniform mat4 u_View;

void main() {
    v_Color = vec4(0.62, 0.78, 0.42, 1);//vec4(0.14, 0.11, 0.11, 1);//vec4(a_Color, 1.0);
    gl_Position = u_Projection * u_View * u_Model * vec4(a_Position, 1);//u_Projection * u_View * u_Model *  vec4(a_Position, 1);
}