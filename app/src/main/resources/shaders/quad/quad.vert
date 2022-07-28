#version 410 core
layout (location = 0) in vec3 aPos;


uniform mat4 u_Projection;
uniform mat4 u_Model;
uniform mat4 u_View;

void main()
{
    gl_Position = vec4(aPos, 1);
}