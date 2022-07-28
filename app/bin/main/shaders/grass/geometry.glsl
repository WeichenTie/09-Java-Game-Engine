#version 410 core

#define PI 3.1415926535

#define MAX_LENGTH 0.7
#define MIN_LENGTH 0.40
#define MAX_ROOT_DISPLACEMENT 0.03
#define MAX_GRASS_SEG 4.0
#define MAX_CURVE 0.0

#define TIP_COLOR vec4(0.78, 0.95, 0.46, 1)
#define UPPER_COLOR vec4(0.67, 0.89, 0.41, 1)
#define BASE_COLOR vec4(0.55, 0.81, 0.36, 1)
//vec4 color = vec4(0.75, 1, 0.21, 1)
layout (points) in;
layout (triangle_strip, max_vertices = 9) out;

uniform mat4 u_Projection;
uniform mat4 u_Model;
uniform mat4 u_View;
uniform float u_Time;

out vec4 g_Color;

float rand(vec2 co){return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);}
/* This code is part of an article, http://gpfault.net/posts/perlin-noise.txt.html */
vec2 grad(vec2 p) {
	vec4 v = vec4(rand(p.xy), rand(p.yx), p.x, p.y);
    return normalize(v.xy*2.0 - vec2(1.0));
}

/* S-shaped curve for 0 <= t <= 1 */
float fade(float t) {
  return t*t*t*(t*(t*6.0 - 15.0) + 10.0);
}


/* 2D noise */
float noise(vec2 p) {
  /* Calculate lattice points. */
  vec2 p0 = floor(p);
  vec2 p1 = p0 + vec2(1.0, 0.0);
  vec2 p2 = p0 + vec2(0.0, 1.0);
  vec2 p3 = p0 + vec2(1.0, 1.0);
  
  /* Look up gradients at lattice points. */
  vec2 g0 = grad(p0);
  vec2 g1 = grad(p1);
  vec2 g2 = grad(p2);
  vec2 g3 = grad(p3);
    
  float t0 = p.x - p0.x;
  float fade_t0 = fade(t0); /* Used for interpolation in horizontal direction */

  float t1 = p.y - p0.y;
  float fade_t1 = fade(t1); /* Used for interpolation in vertical direction. */

  /* Calculate dot products and interpolate.*/
  float p0p1 = (1.0 - fade_t0) * dot(g0, (p - p0)) + fade_t0 * dot(g1, (p - p1)); /* between upper two lattice points */
  float p2p3 = (1.0 - fade_t0) * dot(g2, (p - p2)) + fade_t0 * dot(g3, (p - p3)); /* between lower two lattice points */
  
  /* Calculate final result */
  return (1.0 - fade_t1) * p0p1 + fade_t1 * p2p3;
}


// end of pNoise






mat3 getRotationMatrix(float alpha) {
    alpha = alpha * PI * 2.0;
    return mat3(cos(alpha), 0 , -sin(alpha), 0, 1, 0, sin(alpha), 0, cos(alpha));
}

float calculateHalfWidth(float a, float b, float c, float y) {
    return a * (y + c) * exp(-b * (y + c));
}

float calculateNaturalCurvature(float a, float b, float y) {
    return a * y * exp(y - b);
}

void main()
{
    mat4 MVP = u_Projection * u_View * u_Model;
    vec2 seed = gl_in[0].gl_Position.xz;
    float rotRand = rand(seed * 2.0);
    mat3 rotMat = getRotationMatrix(rotRand);

    float displacementX = (rand(vec2(seed.x * 4.150, rotRand)) - 0.5) * 2.0 * MAX_ROOT_DISPLACEMENT;
    float displacementZ = (rand(vec2(seed.y * 1.0264, rotRand)) - 0.5) * 2.0 * MAX_ROOT_DISPLACEMENT;
    vec4 rootPos = gl_in[0].gl_Position + vec4(displacementX, 0, displacementZ, 0);
    float bladeLength = mix(MAX_LENGTH, MIN_LENGTH, rand(seed));

    vec3 windOffset = vec3(0.0, 0.0, 0.0);
    float wind = noise(gl_in[0].gl_Position.xz + u_Time) * 0.3;

    float windXStrength = wind;
    float windYStrength = 0;//.2 * (sin(scaledTime) + 1/3 * sin(3 * scaledTime) + 1/5 * sin(5 * scaledTime));
    float windZStrength = wind;//0.1 * (sin(scaledTime) + 1/3 * sin(3 * scaledTime) + 1/5 * sin(5 * scaledTime));


    // Get straight grass,
    // Rotate grass
    // Add external forces


    for (float i = 0.0; i < MAX_GRASS_SEG; i+= 1.0) {
        float y = i / MAX_GRASS_SEG; // 0 -> 1;
        float segmentLength = y * bladeLength; // Length of the blade at current segment

        // Calculate blade width
        float width = calculateHalfWidth(0.05, 1, 0.3, y);

        // Calculate blade curve
        float naturalCurveOffset = calculateNaturalCurvature(rotRand, 3, y);


        float bladeSideLen = length(vec3(width, segmentLength, 0)); 
        // Left and rights of the blade after rotation
        vec3 bladeLeft = rotMat * (bladeSideLen * normalize(vec3(-width, segmentLength, naturalCurveOffset)));
        vec3 bladeRight = rotMat * (bladeSideLen * normalize(vec3(width, segmentLength, naturalCurveOffset)));

        windOffset += vec3(windXStrength, windYStrength, windZStrength) * segmentLength / MAX_LENGTH;

        bladeLeft = (bladeSideLen * normalize(bladeLeft + windOffset));
        bladeRight = (bladeSideLen * normalize(bladeRight + windOffset));

        vec4 color = mix(BASE_COLOR, UPPER_COLOR, y);
        gl_Position = MVP*(rootPos + vec4(bladeLeft, 0));
        g_Color = color;
        EmitVertex();

        gl_Position = MVP*(rootPos + vec4(bladeRight, 0));
        g_Color = color;
        EmitVertex();

    }


    // Pseudo wind


    // Levels

    // Tip
    // Blade model coords


    //vec3 tipCoord = rotMat * vec3(0, bladeLength, exp(2-5.0)) + windOffset;
    windOffset += vec3(windXStrength, windYStrength, windZStrength) * bladeLength / MAX_LENGTH;
    
    vec3 tipCoord = bladeLength * normalize(vec3(0, bladeLength, calculateNaturalCurvature(rotRand, 3, 1)));
    tipCoord = bladeLength * normalize(tipCoord);
    gl_Position = MVP*(rootPos + vec4(bladeLength* normalize(rotMat*tipCoord + windOffset), 0.0));
    g_Color = TIP_COLOR;
    EmitVertex();
    
    EndPrimitive();
}